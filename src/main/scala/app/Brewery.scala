package app

import akka.actor.Actor

class Brewery extends Actor {
  override def receive: Receive = {
    case recipe: Recipe =>
  }
}