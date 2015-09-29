package app

import akka.actor.{Actor, ActorRef}

class Masher(boiler: ActorRef) extends Actor {
  override def receive: Receive = {
    case batch: Batch => boiler ! batch
  }
}