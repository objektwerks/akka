package server

import akka.actor.{Actor, ActorRef}
import command.Brew

class Fermenter(conditioner: ActorRef) extends Actor {
  override def receive: Receive = {
    case batch: Brew => conditioner ! batch
  }
}