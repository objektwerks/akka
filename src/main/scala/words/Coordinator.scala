package words

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import words.Master._

import scala.concurrent.duration._

class Coordinator(listener: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case words: Words =>
      val collector = new Collector[Map[String, Int]](30 seconds, words.size, IndexedSeq.empty[Map[String, Int]])
      val master = context.actorOf(Props(new Master(self, collector)), name = newMasterName)
      master ! words
    case CollectorEvent(part, of, data) => listener ! PartialResponse(part, of, data.asInstanceOf[Map[String, Int]])
    case WordsCounted(count) =>
      listener ! Response(count)
      context.stop(sender)
    case PartialWordsCounted(partialCount, cause) =>
      listener ! Response(partialCount, Some(cause))
      context.stop(sender)
  }
}