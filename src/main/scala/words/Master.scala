package words

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, ActorRef, ReceiveTimeout}

import scala.collection.mutable
import scala.concurrent.duration._

class Collector[T](val collect: Int, val collection: mutable.ArrayBuffer[T]) {
  def add(item: T): this.type = {
    collection += item
    this
  }
  def count: Int = collection.size
  def isDone: Boolean = collect == collection.size
}

object Master {
  private val masterNumber = new AtomicInteger()
  private val routerNmumber = new AtomicInteger()
  def newMasterName: String = s"master-${masterNumber.incrementAndGet()}"
  def newRouterName: String = s"router-${routerNmumber.incrementAndGet()}"
}

class Master(coordinator: ActorRef) extends Actor with Router with ActorLogging {
  implicit val ec = context.dispatcher
  val receiveTimeout = 30 seconds
  var collector: Collector[Map[String, Int]] = _

  override def receive: Receive = {
    case words: Words =>
      context.setReceiveTimeout(receiveTimeout)
      collector = new Collector[Map[String, Int]](words.size, mutable.ArrayBuffer.empty[Map[String, Int]])
      words.list foreach {
        words => context.system.scheduler.scheduleOnce(100 millis, router, CountWords(words))
      }
    case WordsCounted(count) =>
      if (collector.add(count).isDone) {
        coordinator ! WordsCounted(WordsCounted.merge(collector.collection))
      }
    case ReceiveTimeout =>
      val partialCount = WordsCounted.merge(collector.collection)
      val cause = s""""Master [${self.path.name}] timed out after ${receiveTimeout.toSeconds} seconds,
                  completing ${collector.count} of ${collector.collect} word counts."""
      coordinator ! PartialWordsCounted(partialCount, cause)
  }
}