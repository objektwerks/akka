package app

import akka.actor.Actor

case class Brew(recipe: Recipe)

case class Brewer() extends Actor {
  override def receive: Receive = {
    case Brew(recipe) => recipe.brew()
  }
}