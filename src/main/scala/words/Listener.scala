package words

import akka.actor.{Actor, ActorLogging, Props}

class Listener extends Actor with ActorLogging {
  val coordinator = context.actorOf(Props(new Coordinator(self)), name = "coordinator")

  override def receive: Receive = {
    case request: Request => coordinator ! CountWordsList(request.words map { words => CountWords(words) })
    case response: Response => log.info(s"Listener received response[${response.count.size}]: $response")
  }
}