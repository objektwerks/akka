package akka

import akka.actor.Actor

case class Step(ingrediants: List[Ingrediant]) extends Actor {
  override def receive: Receive = ???
}