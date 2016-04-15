package words

import akka.actor.{Actor, ActorLogging, ActorRef}

class Client(listener: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case request: Request =>
      log.info("Client received simulate drequest from MasterNode, sent to Listener.")
      listener ! request
    case response: Response => log.info(s"Client received response: $response")
    case fault: Fault => log.error(s"Client received fault: $fault")
  }
}