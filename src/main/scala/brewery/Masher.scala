package brewery

import java.time.LocalDateTime

import akka.actor.{Actor, ActorRef}
import command.Brew
import event.{Mashed, Bottled}

class Masher(boiler: ActorRef) extends Actor {
  override def receive: Receive = {
    case brew: Brew =>
      context.system.eventStream.publish(Mashed(brew.number, LocalDateTime.now()))
      boiler ! brew
  }
}