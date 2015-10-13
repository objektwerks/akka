package actor

import java.time.LocalTime

import akka.actor.{Actor, ActorRef}
import command.Brew
import event.{Cooled, Cooling}
import simulator.Simulator

class Cooler(fermenter: ActorRef) extends Actor {
  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate(39)
      context.system.eventStream.publish(Cooling(brew.number, LocalTime.now()))
      Simulator.simulate(39)
      context.system.eventStream.publish(Cooled(brew.number, LocalTime.now()))
      fermenter ! brew
  }
}