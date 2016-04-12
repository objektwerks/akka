package words

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import akka.util.Timeout
import words.Words._

import scala.concurrent.duration._

class Simulator extends Actor with ActorLogging {
  val router = {
    val routees = Vector.fill(2) {
      val master = context.actorOf(Props[Master])
      context watch master
      ActorRefRoutee(master)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  Cluster(context.system).registerOnMemberUp {
    implicit val ec = context.system.dispatcher
    implicit val timeout = Timeout(3 seconds)
    context.system.scheduler.schedule(2 seconds, 2 seconds) {
      router.route(CountWords(left), sender)
      router.route(CountWords(right), sender)
    }
  }

  override def receive: Receive = {
    case wordsCounted: WordsCounted => log.info(s"Words counted: ${wordsCounted.toString}")
  }
}