package words

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class Coordinator(simulator: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case countWordsList: CountWordsList =>
      val master = context.actorOf(Props(new Master(self)), name = Master.newMasterName)
      master ! countWordsList
    case wordsCounted: WordsCounted =>
      simulator ! Response(wordsCounted.count)
      context.stop(sender)
  }
}