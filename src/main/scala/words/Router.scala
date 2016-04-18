package words

import akka.actor.{Actor, Props, SupervisorStrategy}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.RoundRobinPool

trait Router {
  this: Actor =>

  private val settings = ClusterRouterPoolSettings(totalInstances = 4, maxInstancesPerNode = 2, allowLocalRoutees = false, useRole = Some("worker"))
  private val pool = RoundRobinPool(nrOfInstances = 4, supervisorStrategy = SupervisorStrategy.stoppingStrategy)
  val router = context.actorOf(ClusterRouterPool(pool, settings).props(Props[Worker]), name = Master.newRouterName)
}