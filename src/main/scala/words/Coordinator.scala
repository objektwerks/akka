package words

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class Coordinator(listener: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case countWordsList: CountWordsList =>
      val master = context.actorOf(Props(new Master(self)), name = Master.newMasterName)
      master ! countWordsList
    case wordsCounted: WordsCounted =>
      listener ! Response(wordsCounted.count)
      context.stop(sender)
    case Fault(cause) =>
      listener ! Response(Map[String, Int](), Some(cause))
      context.stop(sender)
  }
}