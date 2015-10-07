package brewery

import akka.actor.{Actor, ActorRef}
import command.Brew

class Conditioner(bottler: ActorRef) extends Actor {
  override def receive: Receive = {
    case batch: Brew => bottler ! batch
  }
}