package cluster

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{ClusterDomainEvent, CurrentClusterState}
import akka.cluster.metrics.StandardMetrics.{Cpu, HeapMemory}
import akka.cluster.metrics.{ClusterMetricsChanged, ClusterMetricsExtension, NodeMetrics}

class ClusterListener extends Actor with ActorLogging {
  val system = context.system
  val cluster = Cluster(system)
  val metrics = ClusterMetricsExtension.get(system)

  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[ClusterDomainEvent])
    metrics.subscribe(self)
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
    metrics.unsubscribe(self)
  }

  override def receive: Receive = {
    case event: ClusterDomainEvent => log.info(s"[${sender.path.name}] $event")
    case state: CurrentClusterState => log.info(s"[${sender.path.name}] $state")
    case ClusterMetricsChanged(clusterMetrics) =>
      clusterMetrics.filter(_.address == cluster.selfAddress) foreach { nodeMetrics =>
        logHeap(nodeMetrics)
        logCpu(nodeMetrics)
      }
  }

  def logHeap(nodeMetrics: NodeMetrics): Unit = nodeMetrics match {
    case HeapMemory(address, timestamp, used, committed, max) =>
      log.debug("Used heap: {} MB", used.doubleValue / 1024 / 1024)
  }

  def logCpu(nodeMetrics: NodeMetrics): Unit = nodeMetrics match {
    case Cpu(address, timestamp, Some(systemLoadAverage), cpuCombined, cpuStolen, processors) =>
      log.debug("Load: {} ({} processors)", systemLoadAverage, processors)
  }
}