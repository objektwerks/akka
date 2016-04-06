package simulation.actor

import akka.actor.{Actor, ActorRef}
import simulation.{Brew, Cooled, Cooling}

class Cooler(fermenter: ActorRef) extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      publisher.publish(Cooling(brew.batch))
      publisher.publish(Cooled(brew.batch))
      fermenter ! brew
  }
}