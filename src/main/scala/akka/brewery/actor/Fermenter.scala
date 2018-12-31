package akka.brewery.actor

import akka.actor.{Actor, ActorRef}
import akka.brewery.{Brew, Fermented, Fermenting}

class Fermenter(conditioner: ActorRef) extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      publisher.publish(Fermenting(brew.batch))
      publisher.publish(Fermented(brew.batch))
      conditioner ! brew
  }
}