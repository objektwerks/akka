package simulation.actor

import akka.actor.{Actor, ActorRef}
import simulation.{Boiled, Boiling, Brew}

class Boiler(cooler: ActorRef) extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      publisher.publish(Boiling(brew.batch))
      publisher.publish(Boiled(brew.batch))
      cooler ! brew
  }
}