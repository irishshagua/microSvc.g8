package de.zalando.spp.brands.http

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{Directive0, Directives, Route}
import akka.stream.ActorMaterializer
import cats.Semigroup
import cats.data.NonEmptyList
import de.zalando.spp.brands.http.routes.Controller
import de.zalando.spp.brands.utils.ActiveActorSystem

import scala.concurrent.ExecutionContext

object PreControllerFilteredRoutes {

  import Directives._

  implicit val numSemigroup: Semigroup[Route] =
    Semigroup.instance((a, b) => a ~ b)

  def removeTrailingSlash: Directive0 =
    mapUnmatchedPath(path => if (path.endsWithSlash) path.reverse.tail.reverse else path)
}

class PreControllerFilteredRoutes(controllers: NonEmptyList[Controller]) extends Controller with ActiveActorSystem {

  implicit val as: ActorSystem            = actorSystem
  implicit val executor: ExecutionContext = as.dispatcher
  implicit val mat: ActorMaterializer     = materializer

  import PreControllerFilteredRoutes._

  val route: Route = LoggingFilter.loggingFilter(removeTrailingSlash(controllers.map(_.route).reduce))
}
