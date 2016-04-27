package words

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorRef, ReceiveTimeout}
import cluster.Collector

import scala.concurrent.duration._

object Master {
  private val masterNumber = new AtomicInteger()
  private val routerNmumber = new AtomicInteger()

  def newMasterName: String = s"master-${masterNumber.incrementAndGet()}"

  def newRouterName: String = s"router-${routerNmumber.incrementAndGet()}"
}

class Master(coordinator: ActorRef, collector: Collector[Map[String, Int]]) extends Actor with Router {
  override def receive: Receive = {
    case words: Words =>
      context.setReceiveTimeout(collector.timeout)
      implicit val ec = context.dispatcher
      words.list foreach {
        words => context.system.scheduler.scheduleOnce(100 millis, router, CountWords(words))
      }
    case WordsCounted(count) =>
      if (collector.add(count).isDone) {
        coordinator ! WordsCounted(WordsCounted.merge(collector.sequence))
        terminate()
      } else {
        coordinator ! collector.event
      }
    case ReceiveTimeout =>
      val partialCount = WordsCounted.merge(collector.sequence)
      val cause = s"Master [${self.path.name}] timed out after ${collector.timeout.toSeconds} seconds, " +
        s"completing ${collector.count} of ${collector.collect} word counts."
      coordinator ! PartialWordsCounted(partialCount, cause)
      terminate()
  }

  def terminate(): Unit = {
    context.stop(router)
    context.stop(self)
  }
}