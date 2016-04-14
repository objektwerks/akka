package words

import akka.actor.{Actor, ActorLogging, Props, SupervisorStrategy}

class Listener extends Actor with ActorLogging {
  val client = context.actorSelection("/user/client")

  override def supervisorStrategy: SupervisorStrategy = SupervisorStrategy.stoppingStrategy

  override def receive: Receive = {
    case request: Request =>
      val master = context.actorOf(Props[Master], name = s"master-${request.uuid}")
      val listOfCountWords = ListOfCountWords(request.words map { words => CountWords(request.uuid, words) })
      master ! listOfCountWords
    case wordsCounted: WordsCounted =>
      client ! Response(wordsCounted)
      context.stop(sender)
    case fault: Fault =>
      client ! fault
      context.stop(sender)
  }
}