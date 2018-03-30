package de.zalando.spp.brands

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.server.{HttpApp, Route}
import cats.data.NonEmptyList
import de.zalando.spp.brands.http.PreControllerFilteredRoutes
import de.zalando.spp.brands.http.routes.HealthAndMetrics
import de.zalando.spp.brands.services._

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

  override protected def routes: Route = applicationRoutesProvider.apiRoutes

  override def waitForShutdownSignal(actorSystem: ActorSystem)(implicit executionContext: ExecutionContext): Future[Done] =
    Promise[Done]().future
}
