package app

import akka.actor.{Actor, ActorRef}

class Conditioner(bottler: ActorRef) extends Actor {
  override def receive: Receive = {
    case batch: Batch => bottler ! batch
  }
}