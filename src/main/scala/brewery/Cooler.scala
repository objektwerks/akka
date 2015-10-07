package brewery

import akka.actor.{Actor, ActorRef}
import command.Brew

class Cooler(fermenter: ActorRef) extends Actor {
  override def receive: Receive = {
    case batch: Brew => fermenter ! batch
  }
}