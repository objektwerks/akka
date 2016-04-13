package words

import akka.actor.Props
import cluster.Node

object MasterNode extends Node {
  system.actorOf(Props[Client], name = "client")
  system.actorOf(Props[Listener], name = "listener")
  system.actorOf(Props[Master], name = "master")
}