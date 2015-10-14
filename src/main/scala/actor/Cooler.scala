package actor

import akka.actor.{Actor, ActorRef}
import command.Brew
import event.{Cooled, Cooling}
import simulator.Simulator

class Cooler(fermenter: ActorRef) extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate()
      publisher.publish(Cooling(brew.batch))
      Simulator.simulate()
      publisher.publish(Cooled(brew.batch))
      fermenter ! brew
  }
}