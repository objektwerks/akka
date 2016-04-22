package words

import java.time.LocalDateTime

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import words.Master._

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration._

class Coordinator(listener: ActorRef) extends Actor with ActorLogging {
  var mastersToIdMapping = TrieMap.empty[ActorRef, Id]

  override def receive: Receive = {
    case request: Request =>
      val words = request.words
      val collector = new Collector[Map[String, Int]](30 seconds, words.size, IndexedSeq.empty[Map[String, Int]])
      val master = context.actorOf(Props(new Master(self, collector)), name = newMasterName)
      mastersToIdMapping += (master -> request.id)
      master ! words
    case CollectorEvent(part, of, data) => listener ! PartialResponse(getId(sender), part, of, data.asInstanceOf[Map[String, Int]])
    case WordsCounted(count) =>
      listener ! Response(removeId(sender), count)
      context.stop(sender)
    case PartialWordsCounted(partialCount, cause) =>
      listener ! Response(removeId(sender), partialCount, Some(cause))
      context.stop(sender)
  }

  private def getId(master: ActorRef): Id = {
    if (mastersToIdMapping.contains(master)) {
      val id = mastersToIdMapping.get(master).get
      id.copy(completed = LocalDateTime.now)
    } else {
      Id(uuid = "Error: Id unavailable!")
    }
  }

  private def removeId(master: ActorRef): Id = {
    if (mastersToIdMapping.contains(master)) {
      val id = mastersToIdMapping.remove(master).get
      id.copy(completed = LocalDateTime.now)
    } else {
      Id(uuid = "Error: Id unavailable!")
    }
  }
}