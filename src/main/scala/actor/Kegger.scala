package actor

import akka.actor.Actor
import command.Brew
import event.{Kegged, Kegging}
import simulator.Simulator

class Kegger extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate()
      publisher.publish(Kegging(brew.batch))
      Simulator.simulate()
      publisher.publish(Kegged(brew.batch))
  }
}