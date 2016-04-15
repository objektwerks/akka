package words

import akka.actor.{Actor, ActorLogging, Props}

class Listener extends Actor with ActorLogging {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case request: Request =>
      log.info(s"Listener received request from Client.")
      val master = context.system.actorOf(Props[Master], name = s"master-${request.uuid}")
      log.info(s"Listener created Master [${master.path.name}].")
      val listOfCountWords = ListOfCountWords(request.words map { words => CountWords(request.uuid, words) })
      master ! listOfCountWords
    case wordsCounted: WordsCounted =>
      log.info("Listener received words counted, and sent response to Client.")
      publisher.publish(Response(wordsCounted))
      context.stop(sender)
    case fault: Fault =>
      log.error("Listener received fault, sent to Client and stopped Master!")
      publisher.publish(fault)
      context.stop(sender)
  }
}