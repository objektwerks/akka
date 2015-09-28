package app

import akka.actor.{ActorRef, ActorSystem, Props}

object BrewMeister extends App {
  val system: ActorSystem = ActorSystem.create("IPAFactory")
  val brewer: ActorRef = system.actorOf(Props[Brewer], name = "brewer")
}