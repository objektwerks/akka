package actor

import akka.actor.{Actor, ActorRef}
import command.Brew
import event.{Mashed, Mashing}
import simulator.Simulator

class Masher(boiler: ActorRef) extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate()
      publisher.publish(Mashing(brew.batch))
      Simulator.simulate()
      publisher.publish(Mashed(brew.batch))
      boiler ! brew
  }
}