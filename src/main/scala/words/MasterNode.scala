package words

import akka.actor.Props
import akka.cluster.Cluster
import akka.util.Timeout
import cluster.{EmbeddedSeedNode, Node}

import scala.concurrent.duration._

object MasterNode extends Node {
  val seedNode2551 = new EmbeddedSeedNode(conf = "words-seed-node.conf", port = 2551, actorSystemName = "words")
  val seedNode2552 = new EmbeddedSeedNode(conf = "words-seed-node.conf", port = 2552, actorSystemName = "words")
  sys.addShutdownHook(seedNode2551.terminate())
  sys.addShutdownHook(seedNode2552.terminate())

  val listener = system.actorOf(Props[Listener], name = "listener")
  val client = system.actorOf(Props(new Client(listener)), name = "client")
  system.eventStream.subscribe(client, classOf[Response])
  system.eventStream.subscribe(client, classOf[Fault])
  system.log.info("MasterNode up!")

  Cluster(system).registerOnMemberUp {
    implicit val ec = system.dispatcher
    implicit val timeout = Timeout(60 seconds)
    system.scheduler.schedule(30 seconds, 9 seconds) {
      system.log.info("MasterNode sent simulated request to Client.")
      listener ! Request()
    }
  }
}