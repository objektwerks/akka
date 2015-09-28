package app

import akka.actor.Actor

class Masher extends Actor {
  override def receive: Receive = {
    case mash: Brew.Mash =>
  }
}