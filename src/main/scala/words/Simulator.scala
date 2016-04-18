package words

import akka.actor.{Actor, ActorLogging, Props}

class Simulator extends Actor with ActorLogging {
  val coordinator = context.actorOf(Props(new Coordinator(self)), name = "coordinator")
  self ! Request(Words.words)

  override def receive: Receive = {
    case request: Request => coordinator ! CountWordsList(request.words map { words => CountWords(words) })
    case response: Response => log.info(s"Simulator received response[${response.count.size}]: $response")
    case fault: Fault => log.error(s"Simulator received fault: $fault")
  }
}