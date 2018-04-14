package de.zalando.spp.brands.client

import com.softwaremill.sttp.Response

object Models {

  // Test Models
  type EnrichedResult[A] = Either[String, (Response[A], A)]

  // HTTP Response Models
  case class HealthState(is_healthy: Boolean, health_check_failures: Option[String])
}
