package words

import akka.actor.Props
import akka.cluster.Cluster
import cluster.Node

object MasterNode extends Node {
  system.log.info("MasterNode up!")
  Cluster(system).registerOnMemberUp {
    system.actorOf(Props[Listener], name = "listener")
    system.actorOf(Props[Client], name = "client")
    system.log.info("MasterNode created listener and client actors.")
  }
}