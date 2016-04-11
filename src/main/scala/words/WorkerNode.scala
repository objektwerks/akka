package words

import akka.actor.Props
import cluster.{ClusterListener, Node}

object WorkerNode extends Node {
  system.actorOf(Props[Worker], name = "worker")
  system.actorOf(Props[ClusterListener], name = "cluster-listener")
}