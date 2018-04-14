package $package$

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.server.{HttpApp, Route}
import cats.data.NonEmptyList
import $package$.http.PreControllerFilteredRoutes
import $package$.http.routes.HealthAndMetrics
import $package$.services._

import scala.concurrent.{ExecutionContext, Future, Promise}

object Server extends HttpApp {

  // Health Checks
  val dummyHealthCheck = new DummyHealthCheck

  // Services
  val metricsSvc    = new KamonMetricsService
  val healthSvc     = new MultiComponentHealthService(NonEmptyList.of(dummyHealthCheck))

  // Controllers
  val healthAndMetrics          = new HealthAndMetrics(metricsSvc, healthSvc)
  val applicationRoutesProvider = new PreControllerFilteredRoutes(NonEmptyList.of(healthAndMetrics))

  override protected def routes: Route = applicationRoutesProvider.route

  override def waitForShutdownSignal(actorSystem: ActorSystem)(implicit executionContext: ExecutionContext): Future[Done] =
    Promise[Done]().future
}
