package actor

import java.time.LocalDateTime

import akka.actor.{Actor, ActorRef}
import command.Brew
import event.Mashed
import simulator.Simulator

class Masher(boiler: ActorRef) extends Actor {
  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate(39)
      context.system.eventStream.publish(brew)
      context.system.eventStream.publish(Mashed(brew.number, LocalDateTime.now()))
      boiler ! brew
  }
}