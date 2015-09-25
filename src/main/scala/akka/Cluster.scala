package akka

import akka.actor.ActorSystem

object Cluster extends App {
  val system: ActorSystem = ActorSystem.create("IPAFactory")
}