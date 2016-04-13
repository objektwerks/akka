package words

import akka.actor.Props
import akka.cluster.Cluster
import cluster.Node

object WorkerNode extends Node {
  Cluster(system).registerOnMemberUp {
    system.actorOf(Props[Worker], name = "worker")
  }
}