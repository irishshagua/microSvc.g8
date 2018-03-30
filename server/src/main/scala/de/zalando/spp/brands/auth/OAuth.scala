package de.zalando.spp.brands.auth

import akka.actor.ActorSystem
import akka.event.slf4j.Logger
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.{AuthenticationDirective, Credentials}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl._
import akka.stream.{Materializer, OverflowStrategy, QueueOfferResult}
import de.zalando.spp.brands.auth.Models.Permission.{NO_PERMISSION, READ_ONLY, READ_WRITE, LIMITED_READ}
import de.zalando.spp.brands.auth.Models._
import de.zalando.spp.brands.utils.ApplicationConfig

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

class OAuth(implicit system: ActorSystem, materializer: Materializer) extends Authentication {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  val logger     = Logger(this.getClass, this.getClass.getSimpleName)
  val authConfig = ApplicationConfig.config.auth

  private val BrandsReadScope  = "spp-brand-service.brands.read"
  private val BrandsWriteScope = "spp-brand-service.brands.write"

  private val EmployeeScoped = "/employees"
  private val ServiceScoped  = "/services"
  import system.dispatcher // to get an implicit ExecutionContext into scope

  private val poolClientFlow = Http().cachedHostConnectionPoolHttps[Promise[HttpResponse]](authConfig.tokenInfoHost)
  private val queue = Source
    .queue[(HttpRequest, Promise[HttpResponse])](authConfig.queueSize, OverflowStrategy.dropNew)
    .via(poolClientFlow)
    .toMat(Sink.foreach({
      case ((Success(resp), p)) => p.success(resp)
      case ((Failure(e), p))    => p.failure(e)
    }))(Keep.left)
    .run()

  override def scoped(): Directive1[TokenAndPermission] = {
    val res: AuthenticationDirective[TokenInfo] = authenticateOAuth2Async("/whatever", authenticator)
    res.map { token =>
      val hasExpired: Boolean = token.expiresIn <= 0
      if (!hasExpired) {
        token.realm match {
          case EmployeeScoped if authConfig.whitelist.contains(token.uid) =>
            TokenAndPermission(token, READ_WRITE)
          case ServiceScoped if token.scope.contains(BrandsWriteScope) =>
            TokenAndPermission(token, READ_WRITE)
          case ServiceScoped if token.scope.contains(BrandsReadScope) =>
            TokenAndPermission(token, READ_ONLY)
          case _ =>
            TokenAndPermission(token, LIMITED_READ)
        }
      } else TokenAndPermission(token, NO_PERMISSION)
    }
  }

  private def authenticator(credentials: Credentials): Future[Option[TokenInfo]] = {
    credentials match {
      case Credentials.Provided(token) =>
        logger.debug("Verifying bearer token")
        val response: Future[HttpResponse] = queueAuthRequest(
          HttpRequest(
            uri = authConfig.tokenInfoUrl.value,
            headers = List(Authorization(OAuth2BearerToken(token)))
          )
        )

        response.flatMap(resp => {
          resp.status match {
            case OK =>
              logger.debug("Bearer token is valid")
              Unmarshal(resp.entity)
                .to[TokenInfo]
                .map(Some(_))

            case st =>
              logger.warn(s"Bearer token is invalid: status $st")
              // ensure the entity is fully consumed if any
              resp.discardEntityBytes()
              Future.successful(None)
          }
        })

      case _ =>
        logger.warn("No bearer token supplied")
        Future.successful(None)
    }
  }

  def queueAuthRequest(request: HttpRequest): Future[HttpResponse] = {
    val responsePromise = Promise[HttpResponse]()
    queue.offer(request -> responsePromise).flatMap {
      case QueueOfferResult.Enqueued => responsePromise.future
      case QueueOfferResult.Dropped =>
        Future.failed(new RuntimeException("The authentication queue has overflowed. Try again later."))
      case QueueOfferResult.Failure(ex) => Future.failed(ex)
      case QueueOfferResult.QueueClosed =>
        Future.failed(
          new RuntimeException(
            "The authentication queue was closed (pool shut down) while running the request. Try again later."))
    }
  }
}
