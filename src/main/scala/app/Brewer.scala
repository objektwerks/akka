package app

import akka.actor.Actor

class Brewer extends Actor {
  override def receive: Receive = {
    case recipe: Recipe =>
      val brewed = recipe.brew()
      sender.tell(brewed, context.parent)
  }
}