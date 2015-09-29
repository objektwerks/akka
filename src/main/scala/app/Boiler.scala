package app

import akka.actor.Actor

class Boiler extends Actor {
  override def receive: Receive = {
    case recipe: Recipe =>
  }
}