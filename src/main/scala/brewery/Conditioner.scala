package brewery

import java.time.LocalDateTime

import akka.actor.{Actor, ActorRef}
import command.Brew
import event.Conditioned
import simulator.Simulator

class Conditioner(bottler: ActorRef) extends Actor {
  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate(39)
      context.system.eventStream.publish(Conditioned(brew.number, LocalDateTime.now()))
      bottler ! brew
  }
}