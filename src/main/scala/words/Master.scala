package words

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, ActorRef, ReceiveTimeout}

import scala.collection.mutable
import scala.concurrent.duration._

object Master {
  private val masterNumber = new AtomicInteger()
  private val routerNmumber = new AtomicInteger()
  def newMasterName: String = s"master-${masterNumber.incrementAndGet()}"
  def newRouterName: String = s"router-${routerNmumber.incrementAndGet()}"
}

class Master(coordinator: ActorRef) extends Actor with Router with ActorLogging {
  implicit val ec = context.dispatcher
  val receiveTimeout = 30 seconds
  val bufferedWordCounts = mutable.ArrayBuffer[Map[String, Int]]()
  var requiredWordCounts = 0

  override def receive: Receive = {
    case countWordsList: CountWordsList =>
      context.setReceiveTimeout(receiveTimeout)
      requiredWordCounts = countWordsList.size
      countWordsList.list foreach {
        countWords => context.system.scheduler.scheduleOnce(100 millis, router, countWords)
      }
    case WordsCounted(count) =>
      bufferedWordCounts += count
      if (bufferedWordCounts.size == requiredWordCounts) {
        coordinator ! WordsCounted(WordsCounted.merge(bufferedWordCounts))
      }
    case ReceiveTimeout =>
      val partialCount = WordsCounted.merge(bufferedWordCounts)
      val cause = s""""Master [${self.path.name}] timed out after ${receiveTimeout.toSeconds} seconds,
                  completing $bufferedWordCounts.size of $requiredWordCounts word counts."""
      coordinator ! PartialWordsCounted(partialCount, cause)
  }
}