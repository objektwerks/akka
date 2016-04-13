package words

import akka.actor.Props
import akka.cluster.Cluster
import akka.util.Timeout
import cluster.Node

import scala.concurrent.duration._

object MasterNode extends Node {
  val listener = system.actorOf(Props[Listener], name = "listener")
  val master = system.actorOf(Props[Master], name = "master")
  system.eventStream.subscribe(master, classOf[CountWords])
  system.eventStream.subscribe(listener, classOf[WordsCounted])

  Cluster(system).registerOnMemberUp {
    import Words._
    implicit val ec = system.dispatcher
    implicit val timeout = Timeout(3 seconds)
    system.scheduler.schedule(3 seconds, 3 seconds) {
      listener ! CountWords(left)
      listener ! CountWords(right)
    }
  }
}