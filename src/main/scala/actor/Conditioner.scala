package actor

import akka.actor.{Actor, ActorRef}
import command.Brew
import event.{Brewed, Conditioned, Conditioning}
import simulator.Simulator

class Conditioner(bottler: ActorRef, kegger: ActorRef, casker: ActorRef) extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate()
      publisher.publish(Conditioning(brew.batch))
      Simulator.simulate()
      publisher.publish(Conditioned(brew.batch))
      publisher.publish(Brewed(brew.batch))
      bottler ! brew
      kegger ! brew
      casker ! brew
  }
}