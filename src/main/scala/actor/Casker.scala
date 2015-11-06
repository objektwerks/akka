package actor

import akka.actor.Actor
import command.Brew
import event.Casked
import state.Casking

class Casker extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      publisher.publish(Casking(brew.batch))
      publisher.publish(Casked(brew.batch))
  }
}