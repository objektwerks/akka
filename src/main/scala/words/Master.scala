package words

import akka.actor.{Actor, ActorLogging, Props, SupervisorStrategy}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool

import scala.collection.mutable

class Master extends Actor with ActorLogging {
  val listener = context.parent
  val router = context.actorOf(ClusterRouterPool(BroadcastPool(2), ClusterRouterPoolSettings(totalInstances = 2,
    maxInstancesPerNode = 1,
    allowLocalRoutees = false,
    useRole = Some("worker"))).props(Props[Worker]), name = "worker-router")
  val listOfWordsCounted = mutable.ArrayBuffer.empty[WordsCounted]
  var numberOfCountWords = 0

  override def supervisorStrategy: SupervisorStrategy = SupervisorStrategy.stoppingStrategy

  override def receive: Receive = {
    case listOfCountWords: ListOfCountWords =>
      numberOfCountWords = listOfCountWords.list.length
      listOfCountWords.list foreach { countWords => router ! countWords }
    case wordsCounted: WordsCounted =>
      listOfWordsCounted += wordsCounted
      numberOfCountWords = numberOfCountWords - 1
      if (numberOfCountWords == 0) listener ! WordsCounted.merge(listOfWordsCounted.toList)
  }
}