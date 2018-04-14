package $package$.utils

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object ActiveActorSystem {
  implicit val actorSystem                     = ActorSystem("some-actor-system") // TODO: templated
  implicit val materializer: ActorMaterializer = ActorMaterializer()
}

trait ActiveActorSystem {
  def actorSystem: ActorSystem        = ActiveActorSystem.actorSystem
  def materializer: ActorMaterializer = ActiveActorSystem.materializer
}
