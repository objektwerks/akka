package words

import akka.actor.{Actor, ActorLogging, Props}

class Listener extends Actor with ActorLogging {
  val master = context.actorOf(Props[Master], name = "master")
  val client = context.actorSelection("/user/client")

  override def receive: Receive = {
    case request: Request => master ! CountWords(request.uuid, request.words)
    case wordsCounted: WordsCounted => client ! Response(wordsCounted)
    case WorkerUnavailable(countWords) => log.error(s"Worker unavailable: $countWords")
  }
}