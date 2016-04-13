package words

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}

class Listener extends Actor with ActorLogging {
  val router = {
    val routees = Vector.fill(2) {
      val master = context.actorOf(Props[Master])
      context watch master
      ActorRefRoutee(master)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  override def receive: Receive = {
    case countWords: CountWords => router.route(countWords, sender)
    case wordsCounted: WordsCounted => log.info(s"Words counted: ${wordsCounted.toString}")
    case WorkerUnavailable(countWords) => log.error(s"Worker unavailable to process: $countWords")
  }
}