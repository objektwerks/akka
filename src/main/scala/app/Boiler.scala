package app

import akka.actor.{Actor, ActorRef, Props}

class Boiler extends Actor {
  val cooler: ActorRef = context.actorOf(Props[Cooler], name = "cooler")

  override def receive: Receive = {
    case batch: Batch => cooler ! batch
  }
}