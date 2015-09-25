package akka

import akka.actor.Actor

case class Brewer(recipe: Recipe) extends Actor {
  override def receive: Receive = ???
}