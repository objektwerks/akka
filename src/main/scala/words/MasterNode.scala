package words

import akka.actor.Props
import akka.cluster.Cluster
import akka.util.Timeout
import cluster.{EmbeddedSeedNode, Node}

import scala.concurrent.duration._

object MasterNode extends Node {
  val seedNode2551 = new EmbeddedSeedNode(conf = "words-seed-node.conf", port = 2551, actorSystem = "words")
  val seedNode2552 = new EmbeddedSeedNode(conf = "words-seed-node.conf", port = 2552, actorSystem = "words")
  sys.addShutdownHook(seedNode2551.terminate())
  sys.addShutdownHook(seedNode2552.terminate())

  Cluster(system).registerOnMemberUp {
    val listener = system.actorOf(Props[Listener], name = "listener")
    val client = system.actorOf(Props(new Client(listener)), name = "client")
    system.eventStream.subscribe(client, classOf[Response])
    system.eventStream.subscribe(client, classOf[Fault])
    system.log.info("MasterNode up!")

    implicit val ec = system.dispatcher
    implicit val timeout = Timeout(60 seconds)
    system.scheduler.scheduleOnce(30 seconds, listener, Request(words = Words.words))
  }
}