package words

import akka.actor.{Actor, ActorLogging, Props}

class Simulator extends Actor with ActorLogging {
  val listener = context.actorOf(Props(new Listener(self)), name = "listener")

  override def receive: Receive = {
    case request: Request => listener ! ListOfCountWords(request.words map { words => CountWords(words) })
    case response: Response => log.info(s"Simulator received response: $response")
    case fault: Fault => log.error(s"Simulator received fault: $fault")
  }
}