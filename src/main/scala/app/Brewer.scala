package app

import akka.actor.Actor

class Brewer extends Actor {
  override def receive: Receive = {
    case recipe: Brew.Recipe =>
  }
}