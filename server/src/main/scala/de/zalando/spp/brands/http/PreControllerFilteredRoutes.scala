package de.zalando.spp.brands.http

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directive0, Directives, Route}
import akka.stream.ActorMaterializer
import cats.Semigroup
import cats.data.NonEmptyList
import cats.implicits._
import de.zalando.spp.brands.auth.Models.Permission.{LIMITED_READ, NO_PERMISSION, READ_ONLY, READ_WRITE}
import de.zalando.spp.brands.auth.{Authentication, OAuth}
import de.zalando.spp.brands.http.routes.Controller
import de.zalando.spp.brands.utils.WithActorSystem

import scala.concurrent.ExecutionContext

object PreControllerFilteredRoutes {

  import Directives._

  implicit val numSemigroup: Semigroup[Route] =
    Semigroup.instance((a, b) => a ~ b)

  def removeTrailingSlash: Directive0 =
    mapUnmatchedPath(path => if (path.endsWithSlash) path.reverse.tail.reverse else path)
}

class PreControllerFilteredRoutes(controllers: NonEmptyList[Controller]) extends Controller with WithActorSystem {

  implicit val as: ActorSystem            = actorSystem
  implicit val executor: ExecutionContext = as.dispatcher
  implicit val mat: ActorMaterializer     = materializer

  import PreControllerFilteredRoutes._

  def authentication: Authentication = new OAuth

  val limitedReadRoutes: Route = controllers.map(_.limitedReadRoutes).reduce
  val readOnlyRoutes: Route    = limitedReadRoutes ~ controllers.map(_.readOnlyRoutes).reduce
  val readWriteRoutes: Route   = readOnlyRoutes ~ controllers.map(_.readWriteRoutes).reduce

  val authenticatedRoutes: Route = {
    val permission = for {
      permission <- authentication.scoped().map(_.permission)
    } yield permission

    permission {
      case LIMITED_READ =>
        limitedReadRoutes
      case READ_ONLY =>
        readOnlyRoutes
      case READ_WRITE =>
        readWriteRoutes
      case NO_PERMISSION =>
        complete(HttpResponse(StatusCodes.Unauthorized))
    }
  }

  val apiRoutes: Route = LoggingFilter.loggingFilter(removeTrailingSlash(limitedReadRoutes ~ authenticatedRoutes))
}
