package brewery

import java.time.LocalDateTime

import akka.actor.{Actor, ActorRef}
import command.Brew
import event.{Fermented, Bottled}
import simulator.Simulator

class Fermenter(conditioner: ActorRef) extends Actor {
  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate(39)
      context.system.eventStream.publish(Fermented(brew.number, LocalDateTime.now()))
      conditioner ! brew
  }
}