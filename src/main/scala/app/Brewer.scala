package app

import akka.actor.Actor

case class Brew(batch: Batch)

case class Brewer() extends Actor {
  override def receive: Receive = {
    case Brew(batch) => batch.brew()
  }
}