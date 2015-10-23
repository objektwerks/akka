package actor

import akka.actor.{Actor, ActorRef}
import command.Brew
import event.{Boiled, Boiling}

class Boiler(cooler: ActorRef) extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      publisher.publish(Boiling(brew.batch))
      publisher.publish(Boiled(brew.batch))
      cooler ! brew
  }
}