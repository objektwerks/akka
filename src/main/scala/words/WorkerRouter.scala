package words

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool

trait WorkerRouter {
  this: Actor =>

  def createWorkerRouter: ActorRef = {
    context.actorOf(
      ClusterRouterPool(BroadcastPool(2),
                        ClusterRouterPoolSettings(totalInstances = 2,
                                                  maxInstancesPerNode = 1,
                                                  allowLocalRoutees = false,
                                                  useRole = Some("worker"))
      ).props(Props[Worker]), name = "worker-router")
  }
}