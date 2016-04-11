package words

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import akka.util.Timeout

import scala.io.Source
import scala.concurrent.duration._

class Listener extends Actor with ActorLogging {
  val words = Source.fromInputStream(getClass.getResourceAsStream("/license.mit")).mkString.split("\\P{L}+")
  val (left, right) = words.splitAt(words.size / 2)
  val router = {
    val routees = Vector.fill(2) {
      val master = context.actorOf(Props[Master])
      context watch master
      ActorRefRoutee(master)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  implicit val ec = context.system.dispatcher
  implicit val timeout = Timeout(3 seconds)
  context.system.scheduler.schedule(2 seconds, 2 seconds) {
    router.route(CountWords(left), sender)
    router.route(CountWords(right), sender)
  }

  override def receive: Receive = {
    case wordsCounted: WordsCounted => log.info(s"Words Counted: ${wordsCounted.toString}")
  }
}