package akka.brewery.actor

import akka.actor.Actor
import akka.brewery.{Brew, Kegged, Kegging}

class Kegger extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      publisher.publish(Kegging(brew.batch))
      publisher.publish(Kegged(brew.batch))
  }
}