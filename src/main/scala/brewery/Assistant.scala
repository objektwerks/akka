package brewery

import akka.actor.{Actor, ActorLogging, ActorRef}
import command.Brew
import event.Stage

class Assistant(masher: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case brew: Brew => masher ! brew
    case stage: Stage => Brewery.stage(stage)
  }
}