package simulation.actor

import akka.actor.Actor
import simulation.{Bottled, Bottling, Brew}

class Bottler extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      publisher.publish(Bottling(brew.batch))
      publisher.publish(Bottled(brew.batch))
  }
}