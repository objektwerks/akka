package words

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class Listener(simulator: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case request: Request =>
      val master = context.actorOf(Props(new Master(self)), name = Master.newMasterName)
      master ! ListOfCountWords(request.words map { words => CountWords(request.uuid, words) })
    case wordsCounted: WordsCounted =>
      simulator ! Response(wordsCounted)
      context.stop(sender)
    case error: Any =>
      simulator ! new Fault(s"Master failed: $error")
      context.stop(sender)
  }
}