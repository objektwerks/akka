package words

import akka.actor.SupervisorStrategy._
import akka.actor.{Actor, Props}
import akka.cluster.routing.{AdaptiveLoadBalancingPool, ClusterRouterPool, ClusterRouterPoolSettings}
import words.Master._

trait Router {
  this: Actor =>

  private val pool = AdaptiveLoadBalancingPool(nrOfInstances = 4, supervisorStrategy = stoppingStrategy)
  private val settings = ClusterRouterPoolSettings(
    totalInstances = 4,
    maxInstancesPerNode = 2,
    allowLocalRoutees = false,
    useRole = Some("worker"))
  val router = context.actorOf(ClusterRouterPool(pool, settings).props(Props[Worker]), name = newRouterName)
}