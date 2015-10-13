package actor

import java.time.LocalTime

import akka.actor.{Actor, ActorRef}
import command.Brew
import event.{Conditioned, Conditioning}
import simulator.Simulator

class Conditioner(bottler: ActorRef) extends Actor {
  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate(39)
      context.system.eventStream.publish(Conditioning(brew.number, LocalTime.now()))
      Simulator.simulate(39)
      context.system.eventStream.publish(Conditioned(brew.number, LocalTime.now()))
      bottler ! brew
  }
}