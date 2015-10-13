package actor

import java.time.LocalTime

import akka.actor.{Actor, ActorRef}
import command.Brew
import event.{Mashed, Mashing}
import simulator.Simulator

class Masher(boiler: ActorRef) extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate(39)
      publisher.publish(Mashing(brew.number, LocalTime.now()))
      Simulator.simulate(39)
      publisher.publish(Mashed(brew.number, LocalTime.now()))
      boiler ! brew
  }
}