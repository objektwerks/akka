package app

import akka.actor.Actor

case class Step(name: String, ingrediants: List[Ingrediant]) extends Actor {
  override def receive: Receive = ???
}