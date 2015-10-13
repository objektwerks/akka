package actor

import java.time.LocalTime

import akka.actor.{Actor, ActorRef}
import command.Brew
import event.{Fermented, Fermenting}
import simulator.Simulator

class Fermenter(conditioner: ActorRef) extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate()
      publisher.publish(Fermenting(brew.number, LocalTime.now()))
      Simulator.simulate()
      publisher.publish(Fermented(brew.number, LocalTime.now()))
      conditioner ! brew
  }
}