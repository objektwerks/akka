package app

import akka.actor.Actor

class Batch extends Actor {
  override def receive: Receive = {
    case Brew(recipe, _, _) =>
      val brewed = recipe.brew()
      sender.tell(brewed, context.parent)
  }
}