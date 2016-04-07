package cluster

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

class Listener extends Actor with ActorLogging {
  override def preStart(): Unit = {
    Cluster(context.system).subscribe(self, classOf[ClusterDomainEvent])
  }

  override def postStop(): Unit = {
    Cluster(context.system).unsubscribe(self)
  }

  override def receive: Receive = {
    case event: ClusterDomainEvent => log.info(s"$event")
    case state: CurrentClusterState => log.info(s"$state")
  }
}