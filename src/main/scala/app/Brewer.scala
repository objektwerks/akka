package app

import akka.actor.{Actor, ActorRef}

class Brewer(masher: ActorRef) extends Actor {
  override def receive: Receive = {
    case batch: Batch => masher ! batch
  }
}