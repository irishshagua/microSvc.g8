package de.zalando.spp.brands.utils

import enumeratum.{EnumEntry, _}
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Interval.Open
import eu.timepit.refined.string.{Uri, Url}
import org.slf4j.{Logger, LoggerFactory}
import shapeless.Nat.{_0 => MinPort}
import shapeless.Witness

// Needed for pure config integrations
import eu.timepit.refined.pureconfig._
import pureconfig.module.enumeratum._

object ApplicationConfig {

  val logger: Logger = LoggerFactory.getLogger("ApplicationConfig")

  type MaxPort   = Witness.`65535`.T
  type ValidPort = Open[MinPort, MaxPort]

  sealed trait Environment extends EnumEntry

  object Environment extends Enum[Environment] {
    val values = findValues
    case object Dev     extends Environment
    case object Staging extends Environment
    case object Prod    extends Environment
  }

  case class HttpConfig(port: Int Refined ValidPort, interface: String)
  case class Deployment(version: String, environment: Environment)
  case class Metrics(prefix: String)
  case class Authorization(tokenInfoHost: String,
                           tokenInfoUrl: String Refined Url,
                           queueSize: Int,
                           newTokenUri: String Refined Uri,
                           tokenQueryParam: String,
                           whitelist: List[String])
  case class ServiceConfig(http: HttpConfig, deployment: Deployment, metrics: Metrics, auth: Authorization)

  val config: ServiceConfig = pureconfig.loadConfig[ServiceConfig]("application") match {
    case Right(appConf) =>
      logger.info(s"Application Config: {}", appConf)
      appConf
    case Left(err) =>
      logger.error("Errors in reading Config: {}", err)
      throw new RuntimeException(s"Config validation failed: $err")
  }
}
