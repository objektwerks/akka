package words

import akka.actor.{Actor, ActorLogging, ActorRef}

class Listener(coordinator: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case request: Request =>
      log.info(s"Listener received request: $request")
      coordinator ! request
  }
}