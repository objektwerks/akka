package actor

import java.time.LocalTime

import akka.actor.{Actor, ActorRef}
import command.Brew
import event.{Mashed, Mashing}
import simulator.Simulator

class Masher(boiler: ActorRef) extends Actor {
  override def receive: Receive = {
    case brew: Brew =>
      context.system.eventStream.publish(brew)
      Simulator.simulate(39)
      context.system.eventStream.publish(Mashing(brew.number, LocalTime.now()))
      Simulator.simulate(39)
      context.system.eventStream.publish(Mashed(brew.number, LocalTime.now()))
      boiler ! brew
  }
}