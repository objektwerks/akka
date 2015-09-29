package app

import akka.actor.{Actor, ActorRef, Props}

class Brewer extends Actor {
  val masher: ActorRef = context.actorOf(Props[Masher], name = "masher")

  override def receive: Receive = {
    case recipe: Recipe => masher ! recipe
  }
}