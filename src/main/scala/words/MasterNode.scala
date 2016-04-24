package words

import akka.actor.Props
import akka.cluster.Cluster
import cluster.{EmbeddedSeedNode, Node}

import scala.io.Source

object MasterNode extends Node {
  val seedNode2551 = new EmbeddedSeedNode(conf = "words-seed-node.conf", port = 2551, actorSystem = "words")
  val seedNode2552 = new EmbeddedSeedNode(conf = "words-seed-node.conf", port = 2552, actorSystem = "words")
  sys.addShutdownHook(seedNode2551.terminate())
  sys.addShutdownHook(seedNode2552.terminate())

  Cluster(system).registerOnMemberUp {
    val list = Source.fromInputStream(getClass.getResourceAsStream("/license.mit")).mkString.split("\\P{L}+").toList
    val words = list.grouped(list.length / 8).toList // list of length 168 / 8 = 21 words per sub list
    val responder = system.actorOf(Props[Responder], name = "responder")
    val coordinator = system.actorOf(Props(new Coordinator(responder)), name = "coordinator")
    val listener = system.actorOf(Props(new Listener(coordinator)), name = "listener")
    val simulator = system.actorOf(Props(new Simulator(listener)), name = "simulator")
    simulator ! words
  }
}