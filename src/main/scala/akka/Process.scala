package akka

import akka.actor.Actor

case class Process(recipe: Recipe) extends Actor {
  override def receive: Receive = ???
}