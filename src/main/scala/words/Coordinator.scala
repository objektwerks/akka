package words

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import words.Master._

import scala.collection.mutable
import scala.concurrent.duration._

class Coordinator(listener: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case words: Words =>
      val collector = new Collector[Map[String, Int]](30 seconds, words.size, mutable.ArrayBuffer.empty[Map[String, Int]])
      val master = context.actorOf(Props(new Master(self, collector)), name = newMasterName)
      master ! words
    case WordsCounted(count) =>
      listener ! Response(count)
      context.stop(sender)
    case PartialWordsCounted(partialCount, cause) =>
      listener ! Response(partialCount, Some(cause))
      context.stop(sender)
  }
}