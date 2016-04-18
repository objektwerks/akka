package words

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class Listener(simulator: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case listOfCountWords: ListOfCountWords =>
      val master = context.actorOf(Props(new Master(self)), name = Master.newMasterName)
      master ! listOfCountWords
    case wordsCounted: WordsCounted =>
      simulator ! Response(wordsCounted.count)
      context.stop(sender)
  }
}