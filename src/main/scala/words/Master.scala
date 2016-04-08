package words

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}

class Master extends Actor with ActorLogging {
  var router = {
    val routees = Vector.fill(2) {
      val worker = context.actorOf(Props[Worker])
      context watch worker
      ActorRefRoutee(worker)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case words: Words => router.route(words, sender())
  }
}