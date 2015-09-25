package akka

import akka.actor.{Props, ActorRef, ActorSystem}

object BrewMeister extends App {
  val system: ActorSystem = ActorSystem.create("IPAFactory")
  val brewer: ActorRef = system.actorOf(Props[Brewer], name = "brewer")
}