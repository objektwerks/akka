package cluster

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.cluster.metrics.ClusterMetricsExtension

class ClusterListener extends Actor with ActorLogging {
  val system = context.system
  val cluster = Cluster(system)
  ClusterMetricsExtension.get(system).subscribe(self)

  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[ClusterDomainEvent])
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  override def receive: Receive = {
    case event: ClusterDomainEvent => log.info(s"[${sender.path.name}] $event")
    case state: CurrentClusterState => log.info(s"[${sender.path.name}] $state")
  }
}