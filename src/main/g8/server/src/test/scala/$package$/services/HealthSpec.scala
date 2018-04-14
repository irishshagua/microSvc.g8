package $package$.services

import cats.data.NonEmptyList
import $package$.services.HealthService.HealthState
import org.scalatest.prop.TableDrivenPropertyChecks._

class HealthSpec extends BaseSpec {

  "Health Spec" should "combine the result of all the health checks" in {
    val healthEvaluations = Table(
      ("checks", "health", "message"),
      (NonEmptyList.of(healthCheck()), true, None),
      (NonEmptyList.of(healthCheck(), healthCheck()), true, None),
      (NonEmptyList.of(healthCheck(), healthCheck(isHealthy = false)), false, Some("Some err")),
      (NonEmptyList.of(healthCheck(isHealthy = false), healthCheck(isHealthy = false)), false, Some("Some errSome err"))
    )

    forAll(healthEvaluations) { (checks: NonEmptyList[ComponentHealthCheck], health: Boolean, message: Option[String]) =>
      val healthSvc = new MultiComponentHealthService(checks)

      val state = healthSvc.healthState
      state.isHealthy shouldBe health
      state.unhealthyDescription shouldBe message
    }
  }

  private def healthCheck(isHealthy: Boolean = true) = new ComponentHealthCheck {
    override def componentState: HealthState = HealthState(
      isHealthy = isHealthy,
      unhealthyDescription = if (isHealthy) None else Some("Some err")
    )
  }
}
