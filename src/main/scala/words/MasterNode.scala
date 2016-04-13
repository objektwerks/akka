package words

import akka.actor.Props
import akka.cluster.Cluster
import cluster.Node

object MasterNode extends Node {
  Cluster(system).registerOnMemberUp {
    system.actorOf(Props[Listener], name = "listener")
    system.actorOf(Props[Client], name = "client")
  }
}