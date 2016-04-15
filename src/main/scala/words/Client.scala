package words

import akka.actor.{Actor, ActorLogging}
import akka.util.Timeout

import scala.concurrent.duration._

class Client extends Actor with ActorLogging {
  implicit val ec = context.dispatcher
  implicit val timeout = Timeout(3 seconds)
  val listener = context.actorSelection("/user/listener")

  context.system.scheduler.schedule(3 seconds, 3 seconds) {
    self ! Request()
    log.info("Client sent request.")
  }

  override def receive: Receive = {
    case request: Request => listener ! Request
    case response: Response => log.info(s"Client received response: $response")
    case fault: Fault => log.error(s"Client received fault: $fault")
  }
}