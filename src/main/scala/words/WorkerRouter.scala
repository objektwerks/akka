package words

import akka.actor.{Actor, ActorRef, Props, SupervisorStrategy}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.RoundRobinPool

trait WorkerRouter {
  this: Actor =>

  def createRouter: ActorRef = {
    val settings = ClusterRouterPoolSettings(totalInstances = 4, maxInstancesPerNode = 2, allowLocalRoutees = false, useRole = Some("worker"))
    val pool = RoundRobinPool(nrOfInstances = 4, supervisorStrategy = SupervisorStrategy.stoppingStrategy)
    context.actorOf(ClusterRouterPool(pool, settings).props(Props[Worker]), name = Master.newRouterName)
  }
}