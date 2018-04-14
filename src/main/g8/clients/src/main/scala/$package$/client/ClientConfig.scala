package $package$.client

import java.net.URI

import org.slf4j.{Logger, LoggerFactory}

object ClientConfig {

  val logger: Logger = LoggerFactory.getLogger("ClientConfig")

  case class HttpConfig(baseUrl: URI)
  case class ClientConfig(http: HttpConfig)

  val config: ClientConfig = {
    val clientConfig = pureconfig.loadConfigOrThrow[ClientConfig]("service-client")
    logger.info(s"Client Config: \$clientConfig")

    clientConfig
  }
}
