package words

import akka.actor.{Actor, ActorLogging, ActorRef}

class Broker(coordinator: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case request: Request =>
      log.info(s"Broker received request: $request")
      coordinator ! request
    case response: Response => log.info(s"Broker received response[${response.count.size}]: $response")
    case response: PartialResponse => log.info(s"Broker received a partial response[${response.count.size}]: $response")
  }
}