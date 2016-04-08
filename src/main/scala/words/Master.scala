package words

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool
import akka.util.Timeout

import scala.concurrent.duration._

class Master extends Actor with ActorLogging {
  implicit val timeout = Timeout(10 seconds)
  val broadcastPool = BroadcastPool(4)
  val clusterRouterPoolSettings = ClusterRouterPoolSettings(
    totalInstances = 4, maxInstancesPerNode = 2, allowLocalRoutees = false, useRole = Some("worker")
  )
  val router = context.actorOf(ClusterRouterPool(broadcastPool, clusterRouterPoolSettings).
    props(Props[Worker]), name = "worker-router")

  def receive = {
    case countWords: CountWords => router ! countWords
    case wordsCounted: WordsCounted => context.system.log.info(wordsCounted.toString)
  }
}