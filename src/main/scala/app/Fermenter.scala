package app

import akka.actor.{Actor, ActorRef, Props}

class Fermenter extends Actor {
  val conditioner: ActorRef = context.actorOf(Props[Conditioner], name = "conditioner")

  override def receive: Receive = {
    case recipe: Recipe =>
  }
}