package app

import akka.actor.{Props, ActorRef, ActorSystem}

class Brewery {
  private val system: ActorSystem = ActorSystem.create("Brewery")
  private val brewer: ActorRef = system.actorOf(Props[Brewer], name = "brewer")

  def brew(recipe: Recipe): Unit = {
    brewer ! recipe
  }
}