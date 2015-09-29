package app

import akka.actor.{Actor, ActorRef}

class Boiler(cooler: ActorRef) extends Actor {
  override def receive: Receive = {
    case batch: Batch => cooler ! batch
  }
}