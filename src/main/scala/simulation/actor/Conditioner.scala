package simulation.actor

import akka.actor.{Actor, ActorRef}
import simulation.command.Brew
import simulation.event.{Brewed, Conditioned}
import simulation.state.Conditioning

class Conditioner(bottler: ActorRef, kegger: ActorRef, casker: ActorRef) extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      publisher.publish(Conditioning(brew.batch))
      publisher.publish(Conditioned(brew.batch))
      publisher.publish(Brewed(brew.batch))
      bottler ! brew
      kegger ! brew
      casker ! brew
  }
}