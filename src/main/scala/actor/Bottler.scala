package actor

import java.time.LocalTime

import akka.actor.Actor
import command.Brew
import event.{Bottled, Brewed}
import simulator.Simulator

class Bottler extends Actor {
  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate(39)
      context.system.eventStream.publish(Bottled(brew.number, LocalTime.now()))
      context.system.eventStream.publish(Brewed(brew.number, brew.initiated, LocalTime.now()))
  }
}