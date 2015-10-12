package actor

import java.time.LocalDateTime

import akka.actor.Actor
import command.Brew
import event.{Bottled, Brewed}
import simulator.Simulator

class Bottler extends Actor {
  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate(39)
      context.system.eventStream.publish(Bottled(brew.number, LocalDateTime.now()))
      context.system.eventStream.publish(Brewed(brew.number, brew.initiated, LocalDateTime.now))
  }
}