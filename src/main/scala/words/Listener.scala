package words

import akka.actor.{Actor, ActorLogging}

class Listener extends Actor with ActorLogging {
  val client = context.actorSelection("/user/client")
  val master = context.actorSelection("/user/master")

  override def receive: Receive = {
    case request: Request => master ! CountWords(request.uuid, request.words)
    case wordsCounted: WordsCounted => client ! Response(wordsCounted)
    case WorkerUnavailable(countWords) => log.error(s"Worker unavailable: $countWords")
  }
}