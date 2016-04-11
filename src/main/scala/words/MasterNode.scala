package words

import akka.actor.Props
import cluster.{ClusterListener, Node}

object MasterNode extends Node {
  val listener = system.actorOf(Props[Listener], name = "listener")
  system.eventStream.subscribe(listener, classOf[WordsCounted])
  system.actorOf(Props[ClusterListener], name = "cluster-listener")
}