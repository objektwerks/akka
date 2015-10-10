package actor

import akka.actor.{Actor, ActorLogging, ActorRef}
import system.Brewery
import command.Brew
import event.Stage

class Assistant(brewer: ActorRef, masher: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case brew: Brew => masher ! brew
    case stage: Stage => Brewery.stage(stage)
  }
}