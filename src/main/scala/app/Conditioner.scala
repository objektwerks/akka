package app

import akka.actor.{Actor, ActorRef, Props}

class Conditioner extends Actor {
  val bottler: ActorRef = context.actorOf(Props[Bottler], name = "bottler")

  override def receive: Receive = {
    case recipe: Recipe => bottler ! recipe
  }
}