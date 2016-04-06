package simulation.actor

import akka.actor.{Actor, ActorRef}
import simulation.command.Brew
import simulation.event.Fermented
import simulation.state.Fermenting

class Fermenter(conditioner: ActorRef) extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      publisher.publish(Fermenting(brew.batch))
      publisher.publish(Fermented(brew.batch))
      conditioner ! brew
  }
}