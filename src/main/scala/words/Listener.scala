package words

import akka.actor.{Actor, ActorLogging, Props}

class Listener extends Actor with ActorLogging {
  val coordinator = context.actorOf(Props(new Coordinator(self)), name = "coordinator")

  override def receive: Receive = {
    case request: Request =>
      log.info(s"Listener received request: $request")
      coordinator ! request
    case response: Response => log.info(s"Listener received response[${response.count.size}]: $response")
    case response: PartialResponse => log.info(s"Listener received a partial response[${response.count.size}]: $response")
  }
}