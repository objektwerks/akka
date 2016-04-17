package words

import akka.actor.{Actor, ActorLogging, Props}
import akka.util.Timeout

import scala.concurrent.duration._

class Simulator extends Actor with ActorLogging {
  val listener = context.actorOf(Props(new Listener(self)), name = "listener")
  implicit val ec = context.system.dispatcher
  implicit val timeout = Timeout(30 seconds)
  context.system.scheduler.scheduleOnce(1 second, listener, Request(words = Words.words))

  override def receive: Receive = {
    case request: Request => listener ! request
    case response: Response => log.info(s"Simulator received response: $response")
    case fault: Fault => log.error(s"Simulator received fault: $fault")
  }
}