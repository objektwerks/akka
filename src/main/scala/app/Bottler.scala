package app

import akka.actor.Actor

class Bottler extends Actor {
  override def receive: Receive = {
    case bootle: Brew.Bottle =>
  }
}