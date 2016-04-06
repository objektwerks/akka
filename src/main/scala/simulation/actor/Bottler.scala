package simulation.actor

import akka.actor.Actor
import simulation.command.Brew
import simulation.event.Bottled
import simulation.state.Bottling

class Bottler extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      publisher.publish(Bottling(brew.batch))
      publisher.publish(Bottled(brew.batch))
  }
}