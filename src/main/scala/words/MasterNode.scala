package words

import akka.actor.Props
import cluster.Node

// Add simulator.
object MasterNode extends Node {
  system.actorOf(Props[Master], name = "master")
}