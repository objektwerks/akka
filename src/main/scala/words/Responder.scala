package words

import akka.actor.{Actor, ActorLogging}

class Responder extends Actor with ActorLogging {
  override def receive: Receive = {
    case response: Response => log.info(s"Responder received response[${response.count.size}]: $response")
    case response: PartialResponse => log.info(s"Responder received a partial response[${response.count.size}]: $response")
  }
}