package actor

import java.time.LocalTime

import akka.actor.Actor
import command.Brew
import event.{Casked, Casking, Brewed}
import simulator.Simulator

class Casker extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate()
      publisher.publish(Casking(brew.number, LocalTime.now()))
      Simulator.simulate()
      publisher.publish(Casked(brew.number, LocalTime.now()))
      publisher.publish(Brewed(brew.number, brew.initiated, LocalTime.now()))
  }
}