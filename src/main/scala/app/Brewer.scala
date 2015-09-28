package app

import akka.actor.{Props, Actor}
import akka.routing.{RoundRobinRoutingLogic, Router, ActorRefRoutee}

class Brewer extends Actor {
  var router = {
    val routees = Vector.fill(3) {
      val routee = context.actorOf(Props[Batch])
      context watch routee
      ActorRefRoutee(routee)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  override def receive: Receive = {
    case recipe: Recipe => router.route(recipe, sender)
  }
}