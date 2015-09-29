package app

import akka.actor.Actor

class Conditioner extends Actor {
  override def receive: Receive = {
    case condition: Condition =>
  }
}