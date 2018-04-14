package $package$.client

import com.softwaremill.sttp._
import com.softwaremill.sttp.json4s._
import Models._
import org.slf4j.{Logger, LoggerFactory}

object ServiceClient {

  val logger: Logger = LoggerFactory.getLogger("ServiceClient")

  implicit val backend = HttpURLConnectionBackend()

  // Service Endpoints
  val HealthEndpoint = "health"

  // Requests
  def applicationHealthCheck: EnrichedResult[HealthState] = {
    val request = s"\${ClientConfig.config.http.baseUrl}/\$HealthEndpoint"
    logger.info(s"Service Client Request: \$request")
    val resp = sttp
      .get(uri"\$request")
      .response(asJson[HealthState])
      .send()

    resp.body.map(resp -> _)
  }
}
