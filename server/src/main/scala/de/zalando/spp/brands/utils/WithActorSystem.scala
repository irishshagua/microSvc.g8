package de.zalando.spp.brands.utils

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object WithActorSystem {
  implicit val actorSystem                     = ActorSystem("some-actor-system") // TODO: templated
  implicit val materializer: ActorMaterializer = ActorMaterializer()
}

trait WithActorSystem {
  def actorSystem: ActorSystem        = WithActorSystem.actorSystem
  def materializer: ActorMaterializer = WithActorSystem.materializer
}
