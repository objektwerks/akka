package app

import akka.actor.Actor

class Batch extends Actor {
  override def receive: Receive = {
    case Brew(recipe) =>
      val brewed = recipe.brew()
      sender.tell(brewed, context.parent)
  }
}