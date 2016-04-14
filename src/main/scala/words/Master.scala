package words

import akka.actor.{Actor, ActorLogging, Props, ReceiveTimeout, SupervisorStrategy}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.RoundRobinPool

import scala.collection.mutable
import scala.concurrent.duration._

class Master extends Actor with ActorLogging {
  val listener = context.parent
  val settings = ClusterRouterPoolSettings(totalInstances = 2, maxInstancesPerNode = 1, allowLocalRoutees = false, useRole = Some("worker"))
  val pool = RoundRobinPool(nrOfInstances = 2, supervisorStrategy = SupervisorStrategy.stoppingStrategy)
  val router = context.actorOf(ClusterRouterPool(pool, settings).props(Props[Worker]), name = "worker-router")
  val listOfWordsCounted = mutable.ArrayBuffer.empty[WordsCounted]
  var numberOfCountWords = 0

  override def receive: Receive = {
    case listOfCountWords: ListOfCountWords =>
      numberOfCountWords = listOfCountWords.list.length
      listOfCountWords.list foreach { countWords => router ! countWords }
      context.setReceiveTimeout(30 seconds)
    case wordsCounted: WordsCounted =>
      listOfWordsCounted += wordsCounted
      numberOfCountWords = numberOfCountWords - 1
      if (numberOfCountWords == 0) listener ! WordsCounted.merge(listOfWordsCounted)
    case ReceiveTimeout => listener ! Fault(s"Master: ${self.path.name} timed out!")
    case _ => listener ! Fault(s"Master: ${self.path.name} failed for unknown reason!")
  }
}