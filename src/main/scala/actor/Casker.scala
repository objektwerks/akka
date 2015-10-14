package actor

import akka.actor.Actor
import command.Brew
import event.{Brewed, Casked, Casking}
import simulator.Simulator

class Casker extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate()
      publisher.publish(Casking(brew.batch))
      Simulator.simulate()
      publisher.publish(Casked(brew.batch))
      publisher.publish(Brewed(brew.batch))
  }
}