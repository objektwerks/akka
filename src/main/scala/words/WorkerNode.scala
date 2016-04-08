package words

import akka.actor.Props
import cluster.Node

object WorkerNode extends Node {
  system.actorOf(Props[Worker], name = "worker")
}