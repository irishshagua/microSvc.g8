package $package$.services

import cats.Semigroup
import cats.data.NonEmptyList
import cats.implicits._
import $package$.services.HealthService._

object HealthService {

  type ComponentError = String

  case class HealthState(isHealthy: Boolean, unhealthyDescription: Option[ComponentError])

  implicit val numSemigroup: Semigroup[HealthState] =
    Semigroup.instance((a, b) => HealthState(a.isHealthy && b.isHealthy, a.unhealthyDescription |+| b.unhealthyDescription))
}

trait HealthService {
  def healthState: HealthState
}

class MultiComponentHealthService(components: NonEmptyList[ComponentHealthCheck]) extends HealthService {

  override def healthState: HealthState = components.map(_.componentState).reduce
}

trait ComponentHealthCheck {
  def componentState: HealthState
}

// Sample Health Check. Replace with actual Impl
class DummyHealthCheck extends ComponentHealthCheck {
  override def componentState: HealthState = HealthState(
    isHealthy = true,
    unhealthyDescription = None
  )
}
