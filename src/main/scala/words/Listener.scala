package words

import akka.actor.{Actor, ActorLogging, Props}

class Listener extends Actor with ActorLogging {
  val client = context.actorSelection("/user/client")

  override def receive: Receive = {
    case request: Request =>
      val master = context.actorOf(Props[Master], name = s"master-${request.uuid}")
      val listOfCountWords = ListOfCountWords(request.words map { words => CountWords(request.uuid, words) })
      master ! listOfCountWords
      log.info("Listener received request, created list of count words and sent to Master.")
    case wordsCounted: WordsCounted =>
      log.info("Listener received words counted, and sent response to Client.")
      client ! Response(wordsCounted)
      context.stop(sender)
    case fault: Fault =>
      log.error("Listener received fault, sent to Client and stopped Master!")
      client ! fault
      context.stop(sender)
  }
}