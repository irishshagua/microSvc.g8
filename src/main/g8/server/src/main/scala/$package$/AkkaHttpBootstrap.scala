package $package$

import $package$.utils.{ApplicationConfig, ActiveActorSystem}

object AkkaHttpBootstrap extends App with ActiveActorSystem {

  val httpConfig = ApplicationConfig.config.http
  Server.startServer(httpConfig.interface, httpConfig.port.value, actorSystem)
}
