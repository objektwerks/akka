package words

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, ActorRef}

import scala.collection.mutable
import scala.concurrent.duration._

object Master {
  private val masterNumber = new AtomicInteger()
  private val routerNmumber = new AtomicInteger()

  def newMasterName: String = s"master-${masterNumber.incrementAndGet()}"

  def newRouterName: String = s"router-${routerNmumber.incrementAndGet()}"
}

class Master(listener: ActorRef) extends Actor with WorkerRouter with ActorLogging {
  implicit val ec = context.dispatcher
  val name = self.path.name
  val router = createRouter
  val bufferOfWordCounts = mutable.ArrayBuffer.empty[Map[String, Int]]
  var requiredNumberOfWordCounts = 0

  override def receive: Receive = {
    case listOfCountWords: ListOfCountWords =>
      requiredNumberOfWordCounts = listOfCountWords.size
      listOfCountWords.list foreach { countWords => context.system.scheduler.scheduleOnce(100 millis, router, countWords) }
    case wordsCounted: WordsCounted =>
      bufferOfWordCounts += wordsCounted.wordCounts
      if (bufferOfWordCounts.size == requiredNumberOfWordCounts) {
        val wordCounts = WordsCounted.merge(bufferOfWordCounts)
        listener ! WordsCounted(wordsCounted.uuid, wordsCounted.assigned, wordCounts)
      }
  }
}