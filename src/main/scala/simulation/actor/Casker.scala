package simulation.actor

import akka.actor.Actor
import simulation.command.Brew
import simulation.event.Casked
import simulation.state.Casking

class Casker extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      publisher.publish(Casking(brew.batch))
      publisher.publish(Casked(brew.batch))
  }
}