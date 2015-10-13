package actor

import java.time.LocalTime

import akka.actor.{Actor, ActorRef}
import command.Brew
import event.{Boiled, Boiling}
import simulator.Simulator

class Boiler(cooler: ActorRef) extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate()
      publisher.publish(Boiling(brew.number, LocalTime.now()))
      Simulator.simulate()
      publisher.publish(Boiled(brew.number, LocalTime.now()))
      cooler ! brew
  }
}