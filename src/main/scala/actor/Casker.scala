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
      publisher.publish(Casking(brew.batch, LocalTime.now()))
      Simulator.simulate()
      publisher.publish(Casked(brew.batch, LocalTime.now()))
      publisher.publish(Brewed(brew.batch, brew.initiated, LocalTime.now()))
  }
}