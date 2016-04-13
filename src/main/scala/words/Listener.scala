package words

import akka.actor.{Actor, ActorLogging}

class Listener extends Actor with ActorLogging {
  val master = context.actorSelection("/user/master")

  override def receive: Receive = {
    case request: Request => master ! CountWords(request.uuid, request.words)
    case wordsCounted: WordsCounted => log.info(s"Words counted: $wordsCounted")
    case WorkerUnavailable(countWords) => log.error(s"Worker unavailable: $countWords")
  }
}