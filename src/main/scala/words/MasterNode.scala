package words

import akka.actor.Props
import cluster.Node

object MasterNode extends Node {
  system.actorOf(Props[Master], name = "master")
}