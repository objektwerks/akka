package actor

import java.time.LocalTime

import akka.actor.{Actor, ActorRef}
import command.Brew
import event.Fermented
import simulator.Simulator

class Fermenter(conditioner: ActorRef) extends Actor {
  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate(39)
      context.system.eventStream.publish(Fermented(brew.number, LocalTime.now()))
      conditioner ! brew
  }
}