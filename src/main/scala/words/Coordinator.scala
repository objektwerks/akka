package words

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import words.Master._

class Coordinator(listener: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case words: Words =>
      val master = context.actorOf(Props(new Master(self)), name = newMasterName)
      master ! words
    case WordsCounted(count) =>
      listener ! Response(count)
      context.stop(sender)
    case PartialWordsCounted(partialCount, cause) =>
      listener ! Response(partialCount, Some(cause))
      context.stop(sender)
  }
}