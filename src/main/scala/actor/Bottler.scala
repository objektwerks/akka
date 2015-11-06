package actor

import akka.actor.Actor
import command.Brew
import event.Bottled
import state.Bottling

class Bottler extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      publisher.publish(Bottling(brew.batch))
      publisher.publish(Bottled(brew.batch))
  }
}