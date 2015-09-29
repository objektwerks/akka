package app

import akka.actor.{Actor, ActorRef, Props}

class Masher extends Actor {
  val boiler: ActorRef = context.actorOf(Props[Boiler], name = "boiler")

  override def receive: Receive = {
    case recipe: Recipe =>
  }
}