package app

import akka.actor.{Actor, ActorRef, Props}

class Cooler extends Actor {
  val fermenter: ActorRef = context.actorOf(Props[Fermenter], name = "fermenter")

  override def receive: Receive = {
    case recipe: Recipe => fermenter ! recipe
  }
}