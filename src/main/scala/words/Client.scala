package words

import akka.actor.{Actor, ActorLogging, Props}
import akka.util.Timeout

import scala.concurrent.duration._

class Client extends Actor with ActorLogging {
  val listener = context.actorOf(Props[Listener], name = "listener")
  implicit val ec = context.system.dispatcher
  implicit val timeout = Timeout(30 seconds)
  context.system.scheduler.scheduleOnce(30 seconds, listener, Request(words = Words.words))

  override def receive: Receive = {
    case request: Request => listener ! request
    case response: Response => log.info(s"Client received response: $response")
    case fault: Fault => log.error(s"Client received fault: $fault")
  }
}