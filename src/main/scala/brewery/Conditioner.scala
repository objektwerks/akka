package brewery

import java.time.LocalDateTime

import akka.actor.{Actor, ActorRef}
import command.Brew
import event.{Conditioned, Bottled}

class Conditioner(bottler: ActorRef) extends Actor {
  override def receive: Receive = {
    case brew: Brew =>
      context.system.eventStream.publish(Conditioned(brew.number, LocalDateTime.now()))
      bottler ! brew
  }
}