package actor

import akka.actor.Actor
import command.Brew
import event._
import simulator.Simulator

class Bottler extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate()
      publisher.publish(Bottling(brew.batch))
      Simulator.simulate()
      publisher.publish(Bottled(brew.batch))
  }
}