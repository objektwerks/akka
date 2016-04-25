package words

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import words.Master._

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration._

class Coordinator(listener: ActorRef) extends Actor with ActorLogging {
  val masterToIdMapping = TrieMap.empty[ActorRef, Id]

  override def receive: Receive = {
    case request: Request =>
      val words = request.words
      val collector = new Collector[Map[String, Int]](30 seconds, words.size, IndexedSeq.empty[Map[String, Int]])
      val master = context.actorOf(Props(new Master(self, collector)), name = newMasterName)
      masterToIdMapping += (master -> request.id)
      master ! words
    case CollectorEvent(part, of, data) =>
      val id = getId(sender, remove = false)
      listener ! PartialResponse(id, part, of, data.asInstanceOf[Map[String, Int]])
    case WordsCounted(count) =>
      val id = getId(sender, remove = true)
      listener ! Response(id, count)
      context.stop(sender)
    case PartialWordsCounted(partialCount, cause) =>
      val id = getId(sender, remove = true)
      listener ! Response(id, partialCount, Some(cause))
      context.stop(sender)
  }

  def getId(master: ActorRef, remove: Boolean): Id = {
    if (masterToIdMapping.contains(master)) {
      val id = if (remove) masterToIdMapping.remove(master).get else masterToIdMapping.get(master).get
      id.toCopy(id)
    } else {
      Id(uuid = "Error: Id unavailable!")
    }
  }
}