package simulation.actor

import akka.actor.{Actor, ActorRef}
import simulation.command.Brew
import simulation.event.Mashed
import simulation.state.Mashing

class Masher(boiler: ActorRef) extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      publisher.publish(Mashing(brew.batch))
      publisher.publish(Mashed(brew.batch))
      boiler ! brew
  }
}