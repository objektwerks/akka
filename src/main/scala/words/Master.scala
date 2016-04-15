package words

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, Props, ReceiveTimeout, SupervisorStrategy}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.RoundRobinPool

import scala.collection.mutable
import scala.concurrent.duration._

object Master {
  private val routerNmumber = new AtomicInteger()
  def nextRouterNumber: Int = routerNmumber.incrementAndGet()
}

class Master extends Actor with ActorLogging {
  val listener = context.parent
  val settings = ClusterRouterPoolSettings(totalInstances = 4, maxInstancesPerNode = 2, allowLocalRoutees = false, useRole = Some("worker"))
  val pool = RoundRobinPool(nrOfInstances = 4, supervisorStrategy = SupervisorStrategy.stoppingStrategy)
  val router = context.actorOf(ClusterRouterPool(pool, settings).props(Props[Worker]), name = s"router-${Master.nextRouterNumber}")
  val listOfWordsCounted = mutable.ArrayBuffer.empty[WordsCounted]
  var numberOfCountWords = 0
  log.info(s"Master [${self.path.name}] created by Listener.")

  override def receive: Receive = {
    case listOfCountWords: ListOfCountWords =>
      log.info("Master received list of count words, and routed it to workers.")
      numberOfCountWords = listOfCountWords.list.length
      listOfCountWords.list foreach { countWords => router ! countWords }
      context.setReceiveTimeout(60 seconds)
    case wordsCounted: WordsCounted =>
      log.info("Master received words counted.")
      listOfWordsCounted += wordsCounted
      numberOfCountWords = numberOfCountWords - 1
      if (numberOfCountWords == 0) {
        listener ! WordsCounted.merge(listOfWordsCounted)
        log.info("Master merged listed of words counted, and sent to Listener.")
      }
    case ReceiveTimeout =>
      log.error("Master received timeout, sent fault to Listener.")
      listener ! Fault(s"Master: ${self.path.name} timed out!")
    case _ =>
      log.error("Master received unknown message, sent as fault to Listener.")
      listener ! Fault(s"Master: ${self.path.name} from ${sender.path.name} failed for unknown reason!")
  }
}