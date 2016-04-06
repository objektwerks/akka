package brewery.actor

import akka.actor.{Actor, ActorRef}
import brewery.{Brew, Mashed, Mashing}

class Masher(boiler: ActorRef) extends Actor {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case brew: Brew =>
      publisher.publish(Mashing(brew.batch))
      publisher.publish(Mashed(brew.batch))
      boiler ! brew
  }
}