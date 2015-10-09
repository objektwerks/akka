package brewery

import java.time.LocalDateTime

import akka.actor.{Actor, ActorRef}
import command.Brew
import event.{Cooled, Bottled}

class Cooler(fermenter: ActorRef) extends Actor {
  override def receive: Receive = {
    case brew: Brew =>
      context.system.eventStream.publish(Cooled(brew.number, LocalDateTime.now()))
      fermenter ! brew
  }
}