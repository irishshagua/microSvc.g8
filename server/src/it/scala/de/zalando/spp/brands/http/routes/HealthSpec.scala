package de.zalando.spp.brands.http.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.zalando.spp.brands.services.HealthService.HealthState
import de.zalando.spp.brands.services.{HealthService, MetricsService}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}
import org.mockito.Mockito._

class HealthSpec extends FlatSpec with Matchers with MockitoSugar with ScalatestRouteTest with BeforeAndAfterEach {

  val mockHealthSvc = mock[HealthService]
  val mockMetricsSvc = mock[MetricsService]

  val healthRoute = new HealthAndMetrics(mockMetricsSvc, mockHealthSvc)

  override def beforeEach(): Unit = {
    reset(mockHealthSvc)
  }

  "GET /health" should "be available at all auth levels" in {
    Get("/health") ~> healthRoute.limitedReadRoutes ~> check {
      handled shouldBe true
    }

    Get("/health") ~> healthRoute.readOnlyRoutes ~> check {
      handled shouldBe true
    }

    Get("/health") ~> healthRoute.readWriteRoutes ~> check {
      handled shouldBe true
    }
  }

  it should "return a 200 for a healthy endpoint" in {
    when(mockHealthSvc.healthState).thenReturn(HealthState(isHealthy = true, None))
    Get("/health") ~> healthRoute.readWriteRoutes ~> check {
      status shouldBe StatusCodes.OK
    }
  }

  it should "return a 500 for a unhealthy endpoint" in {
    when(mockHealthSvc.healthState).thenReturn(HealthState(isHealthy = false, Some("gremlins")))
    Get("/health") ~> healthRoute.readWriteRoutes ~> check {
      status shouldBe StatusCodes.InternalServerError
    }
  }
}
