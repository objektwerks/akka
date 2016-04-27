package words

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import cluster.{Collector, CollectorEvent}
import words.Master._

import scala.collection.concurrent.TrieMap
import scala.collection.mutable
import scala.concurrent.duration._

class Coordinator(broker: ActorRef) extends Actor with ActorLogging {
  val masterToIdMapping = TrieMap.empty[ActorRef, Id]

  override def receive: Receive = {
    case request: Request =>
      val words = request.words
      val collector = new Collector[Map[String, Int]](30 seconds, words.size, new mutable.ArrayBuffer[Map[String, Int]](words.size))
      val master = context.actorOf(Props(new Master(self, collector)), name = newMasterName)
      masterToIdMapping += (master -> request.id)
      log.info(s"Coordinator created Master [${master.path.name}]")
      master ! words
    case CollectorEvent(part, of, data) =>
      val id = getUpdatedId(sender, remove = false)
      broker ! Notification(id, part, of, data.asInstanceOf[Map[String, Int]])
    case WordsCounted(count) =>
      val id = getUpdatedId(sender, remove = true)
      broker ! Response(id, count)
    case PartialWordsCounted(partialCount, cause) =>
      val id = getUpdatedId(sender, remove = true)
      broker ! Response(id, partialCount, Some(cause))
  }

  def getUpdatedId(master: ActorRef, remove: Boolean): Id = {
    val oldId = masterToIdMapping.get(master).get
    val newId = oldId.toUpdatedCopy
    if (remove) {
      require(masterToIdMapping.remove(master, oldId))
    } else {
      require(masterToIdMapping.replace(master, oldId, newId))
    }
    newId
  }
}