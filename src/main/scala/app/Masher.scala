package app

import akka.actor.{Props, ActorRef, Actor}

class Masher extends Actor {
  val boiler: ActorRef = context.actorOf(Props[Boiler], name = "boiler")

  override def receive: Receive = {
    case mash: Mash => boiler ! Boil(mash.recipe)
  }
}