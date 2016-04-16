package words

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, ReceiveTimeout}
import akka.util.Timeout

import scala.collection.mutable
import scala.concurrent.duration._

object Master {
  private val masterNumber = new AtomicInteger()
  private val routerNmumber = new AtomicInteger()

  def newMasterName: String = s"master-${masterNumber.incrementAndGet()}"

  def newRouterName: String = s"router-${routerNmumber.incrementAndGet()}"
}

class Master extends Actor with WorkerRouter with ActorLogging {
  implicit val ec = context.dispatcher
  implicit val timeout = Timeout(30 seconds)
  val listener = context.parent
  val name = self.path.name
  val router = createRouter
  val listOfWordsCounted = mutable.ArrayBuffer.empty[WordsCounted]
  var numberOfCountWords = 0

  override def receive: Receive = {
    case listOfCountWords: ListOfCountWords =>
      log.info(s"Master [$name] received list of count words, and routed it to workers.")
      numberOfCountWords = listOfCountWords.list.length
      listOfCountWords.list foreach { countWords => context.system.scheduler.scheduleOnce(10 millis, router, countWords) }
      context.setReceiveTimeout(30 seconds)
    case wordsCounted: WordsCounted =>
      listOfWordsCounted += wordsCounted
      numberOfCountWords = numberOfCountWords - 1
      if (numberOfCountWords == 0) listener ! WordsCounted.merge(listOfWordsCounted)
    case ReceiveTimeout => listener ! Fault(s"Master [$name] timed out!")
    case _ => listener ! Fault(s"Master [$name] from ${sender.path.name} failed for unknown reason!")
  }
}