package server

import akka.actor.{Actor, ActorRef}
import command.Brew

class Boiler(cooler: ActorRef) extends Actor {
  override def receive: Receive = {
    case batch: Brew => cooler ! batch
  }
}