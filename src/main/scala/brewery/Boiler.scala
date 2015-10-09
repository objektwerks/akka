package brewery

import java.time.LocalDateTime

import akka.actor.{Actor, ActorRef}
import command.Brew
import event.Boiled
import simulator.Simulator

class Boiler(cooler: ActorRef) extends Actor {
  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate(39)
      context.system.eventStream.publish(Boiled(brew.number, LocalDateTime.now()))
      cooler ! brew
  }
}