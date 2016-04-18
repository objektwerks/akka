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

class Master(coordinator: ActorRef) extends Actor with WorkerRouter with ActorLogging {
  implicit val ec = context.dispatcher
  val router = createRouter
  val bufferedWordCounts = mutable.ArrayBuffer.empty[Map[String, Int]]
  var requiredWordCounts = 0

  override def receive: Receive = {
    case countWordsList: CountWordsList =>
      requiredWordCounts = countWordsList.size
      countWordsList.list foreach { countWords => context.system.scheduler.scheduleOnce(100 millis, router, countWords) }
    case wordsCounted: WordsCounted =>
      bufferedWordCounts += wordsCounted.count
      if (bufferedWordCounts.size == requiredWordCounts) coordinator ! WordsCounted(wordsCounted.merge(bufferedWordCounts))
  }
}