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
  val listOfWordsCounted = mutable.ArrayBuffer.empty[WordsCounted]
  var countdown = 0

  override def receive: Receive = {
    case listOfCountWords: ListOfCountWords =>
      log.info(s"Master [$name] received list of count words [${listOfCountWords.list.size}], and routed it to workers.")
      countdown = listOfCountWords.list.size
      listOfCountWords.list foreach { countWords => context.system.scheduler.scheduleOnce(100 millis, router, countWords) }
    case wordsCounted: WordsCounted =>
      log.info(s"\nMaster words counted: $wordsCounted")
      listOfWordsCounted += wordsCounted
      countdown = countdown - 1
      log.info(s"\n words counted countdown = $countdown")
      if (countdown == 0) {
        log.info(s"\nMaster final list of words counts: $listOfWordsCounted")
        val counts = WordsCounted.merge(listOfWordsCounted)
        log.info(s"\nMaster merged list of words counted = ${listOfWordsCounted.size}, total counts: $counts")
        listener ! WordsCounted(wordsCounted.uuid, wordsCounted.assigned, counts)
      }
  }
}