package brewery.actor

import akka.actor.Actor
import brewery.{Brew, Casked, Casking}

class Casker extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      publisher.publish(Casking(brew.batch))
      publisher.publish(Casked(brew.batch))
  }
}