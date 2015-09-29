package app

import akka.actor.{Actor, ActorRef}

class Fermenter(conditioner: ActorRef) extends Actor {
  override def receive: Receive = {
    case batch: Batch => conditioner ! batch
  }
}