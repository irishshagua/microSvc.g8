package $package$.http.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import $package$.http.ErrorHandler._
import $package$.http.protocol.Protocol
import $package$.services.HealthService.HealthState
import $package$.services.{HealthService, MetricsService}

object HealthAndMetrics {

  val Metrics = "Metrics"
  val Health  = "Health"
}

class HealthAndMetrics(metricsSvc: MetricsService, healthSvc: HealthService)
    extends Controller
    with Protocol {

  private val DefaultHealthErrMsg = "Unknown component Error caused health check failure"

  import HealthAndMetrics._
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  val metricsRoute: Route = (path(Metrics.toLowerCase) & get & metricName(Metrics)) {
    complete(metricsSvc.allMetrics)
  }

  val healthRoute: Route = (path(Health.toLowerCase) & get & metricName(Health)) {
    healthSvc.healthState match {
      case HealthState(false, Some(errMsg)) => completeWithProblemJson(StatusCodes.InternalServerError, errMsg)
      case HealthState(false, None)         => completeWithProblemJson(StatusCodes.InternalServerError, DefaultHealthErrMsg)
      case hs @ HealthState(true, _)        => complete(hs)
    }
  }

  val route: Route = metricsRoute ~ healthRoute
}
