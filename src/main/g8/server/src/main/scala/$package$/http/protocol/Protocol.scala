package $package$.http.protocol

import $package$.services.HealthService.HealthState
import io.circe.Encoder

trait Protocol {

  implicit val encodeHealthState: Encoder[HealthState] =
    Encoder.forProduct2("is_healthy", "health_check_failures")(hs => (hs.isHealthy, hs.unhealthyDescription))
}
