package app

import akka.actor.Actor

class Cooler extends Actor {
  override def receive: Receive = {
    case cool: Cool =>
  }
}