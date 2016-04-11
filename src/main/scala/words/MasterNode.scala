package words

import akka.actor.Props
import cluster.Node

object MasterNode extends Node {
  val listener = system.actorOf(Props[Listener], name = "listener")
  system.eventStream.subscribe(listener, classOf[WordsCounted])
}