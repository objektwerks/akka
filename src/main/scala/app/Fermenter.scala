package app

import akka.actor.Actor

class Fermenter extends Actor {
  override def receive: Receive = {
    case recipe: Recipe =>
  }
}