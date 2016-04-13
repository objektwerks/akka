package words

import akka.actor.Props
import akka.cluster.Cluster
import akka.util.Timeout
import cluster.Node

import scala.concurrent.duration._

object MasterNode extends Node {
  system.actorOf(Props[Listener], name = "listener")
  system.actorOf(Props[Master], name = "master")

  Cluster(system).registerOnMemberUp {
    implicit val ec = system.dispatcher
    implicit val timeout = Timeout(3 seconds)
    val listener = system.actorSelection("/user/listener")
    system.scheduler.schedule(3 seconds, 3 seconds) {
      listener ! Request()
    }
  }
}