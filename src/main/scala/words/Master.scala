package words

import akka.actor.{Actor, ActorLogging, ActorRef, Props, SupervisorStrategy}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool

import scala.collection.mutable

class Master extends Actor with ActorLogging {
  val listener = context.parent
  val workers = mutable.ArrayBuffer.empty[ActorRef]
  val listOfWordsCount = mutable.ArrayBuffer.empty[WordsCounted]
  val router = context.actorOf(ClusterRouterPool(BroadcastPool(2), ClusterRouterPoolSettings(totalInstances = 2,
    maxInstancesPerNode = 1,
    allowLocalRoutees = false,
    useRole = Some("worker"))).props(Props[Worker]), name = "worker-router")

  override def supervisorStrategy: SupervisorStrategy = SupervisorStrategy.stoppingStrategy

  override def receive: Receive = {
    case listOfCountWords: ListOfCountWords => listOfCountWords.list foreach { countWords => router ! countWords }
    case wordsCounted: WordsCounted => listOfWordsCount += wordsCounted
  }
}