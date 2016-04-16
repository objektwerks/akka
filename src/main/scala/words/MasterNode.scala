package words

import akka.actor.Props
import akka.cluster.Cluster
import cluster.{EmbeddedSeedNode, Node}

object MasterNode extends Node {
  val seedNode2551 = new EmbeddedSeedNode(conf = "words-seed-node.conf", port = 2551, actorSystem = "words")
  val seedNode2552 = new EmbeddedSeedNode(conf = "words-seed-node.conf", port = 2552, actorSystem = "words")
  sys.addShutdownHook(seedNode2551.terminate())
  sys.addShutdownHook(seedNode2552.terminate())

  Cluster(system).registerOnMemberUp {
    system.actorOf(Props[Client], name = "client")
  }
}