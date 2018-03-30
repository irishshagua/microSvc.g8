package de.zalando.spp.brands

import de.zalando.spp.brands.utils.{ApplicationConfig, WithActorSystem}

object AkkaHttpBootstrap extends App with WithActorSystem {

  val httpConfig = ApplicationConfig.config.http
  Server.startServer(httpConfig.interface, httpConfig.port.value, actorSystem)
}
