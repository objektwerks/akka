package actor

import java.time.LocalTime

import akka.actor.Actor
import command.Brew
import event.{Bottled, Bottling, Brewed}
import simulator.Simulator

class Bottler extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate(39)
      publisher.publish(Bottling(brew.number, LocalTime.now()))
      Simulator.simulate(39)
      publisher.publish(Bottled(brew.number, LocalTime.now()))
      publisher.publish(Brewed(brew.number, brew.initiated, LocalTime.now()))
  }
}