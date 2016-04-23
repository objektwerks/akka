package words

import akka.actor.{Actor, ActorLogging, ActorSelection}

class Listener extends Actor with ActorLogging {
  override def receive: Receive = {
    case request: Request =>
      log.info(s"Listener received request: $request")
      listener ! request
  }

  def listener: ActorSelection = context.actorSelection("/user/listener")
}