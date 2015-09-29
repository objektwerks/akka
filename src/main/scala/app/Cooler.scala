package app

import akka.actor.{Actor, ActorRef}

class Cooler(fermenter: ActorRef) extends Actor {
  override def receive: Receive = {
    case batch: Batch => fermenter ! batch
  }
}