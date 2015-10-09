package brewery

import java.time.LocalDateTime

import akka.actor.{Actor, ActorRef}
import command.Brew
import event.Boiled

class Boiler(cooler: ActorRef) extends Actor {
  override def receive: Receive = {
    case brew: Brew =>
      context.system.eventStream.publish(Boiled(brew.number, LocalDateTime.now()))
      cooler ! brew
  }
}