package server

import akka.actor.{Actor, ActorRef}
import command.Brew

class Masher(boiler: ActorRef) extends Actor {
  override def receive: Receive = {
    case batch: Brew => boiler ! batch
  }
}