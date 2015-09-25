package akka

import akka.actor.{Props, ActorRef, ActorSystem}

object Brewery extends App {
  val system: ActorSystem = ActorSystem.create("IPAFactory")
  val process: ActorRef = system.actorOf(Props[Brewer], name = "process")
  val step: ActorRef = system.actorOf(Props[Step], name = "step")
}