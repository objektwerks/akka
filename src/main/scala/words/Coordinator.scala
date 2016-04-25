package words

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import words.Master._

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration._

class Coordinator(broker: ActorRef) extends Actor with ActorLogging {
  val masterToIdMapping = TrieMap.empty[ActorRef, Id]

  override def receive: Receive = {
    case request: Request =>
      val words = request.words
      val collector = new Collector[Map[String, Int]](30 seconds, words.size, IndexedSeq.empty[Map[String, Int]])
      val master = context.actorOf(Props(new Master(self, collector)), name = newMasterName)
      masterToIdMapping += (master -> request.id)
      master ! words
    case CollectorEvent(part, of, data) => broker ! PartialResponse(getId(sender, remove = false), part, of, data.asInstanceOf[Map[String, Int]])
    case WordsCounted(count) =>
      broker ! Response(getId(sender, remove = true), count)
      context.stop(sender)
    case PartialWordsCounted(partialCount, cause) =>
      broker ! Response(getId(sender, remove = true), partialCount, Some(cause))
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