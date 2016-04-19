package words

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class Coordinator(listener: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case countWordsList: CountWordsList =>
      val master = context.actorOf(Props(new Master(self)), name = Master.newMasterName)
      master ! countWordsList
    case WordsCounted(count) =>
      listener ! Response(count)
      context.stop(sender)
    case PartialWordsCounted(partialCount, timeout) =>
      listener ! Response(partialCount, Some(timeout))
      context.stop(sender)
  }
}