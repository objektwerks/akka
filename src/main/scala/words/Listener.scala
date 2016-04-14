package words

import akka.actor.{Actor, ActorLogging, Props, SupervisorStrategy}

class Listener extends Actor with ActorLogging {
  val master = context.actorOf(Props[Master], name = "master")
  val client = context.actorSelection("/user/client")

  override def supervisorStrategy: SupervisorStrategy = SupervisorStrategy.stoppingStrategy

  override def receive: Receive = {
    case request: Request => request.words foreach { words => master ! CountWords(request.uuid, words) }
    case wordsCounted: WordsCounted => client ! Response(wordsCounted)
    case WorkerUnavailable(countWords) => log.error(s"Worker unavailable: $countWords")
  }
}