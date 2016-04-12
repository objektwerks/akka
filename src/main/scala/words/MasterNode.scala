package words

import akka.actor.Props
import cluster.Node

object MasterNode extends Node {
  val simulator = system.actorOf(Props[Simulator], name = "simulator")
  system.eventStream.subscribe(simulator, classOf[WordsCounted])
}