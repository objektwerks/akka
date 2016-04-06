package simulation.actor

import akka.actor.Actor
import simulation.{Brew, Kegged, Kegging}

class Kegger extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      publisher.publish(Kegging(brew.batch))
      publisher.publish(Kegged(brew.batch))
  }
}