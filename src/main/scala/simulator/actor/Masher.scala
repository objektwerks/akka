package simulator.actor

import akka.actor.{Actor, ActorRef}
import simulator.{Brew, Mashed, Mashing}

class Masher(boiler: ActorRef) extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      publisher.publish(Mashing(brew.batch))
      publisher.publish(Mashed(brew.batch))
      boiler ! brew
  }
}