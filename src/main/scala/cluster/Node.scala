package cluster

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._

abstract class Node extends App {
  implicit val timeout = Timeout(10 seconds)

  if (args.length < 3) {
    println("Please, provide a (1) conf; (2) port; and (3) role.")
    System.exit(-1)
  }

  val conf = args(0)
  val port = args(1).toInt
  val actorSystem = args(2)
  println(s"Loading conf: $conf on port: $port for $actorSystem.")

  val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port = $port").withFallback(ConfigFactory.load(conf))

  val system = ActorSystem.create(actorSystem, config)
  system.actorOf(Props[ClusterListener], name = "cluster-listener")
  system.log.info(s"Node initialized with $conf on port: $port for $actorSystem!")

  sys.addShutdownHook {
    implicit val ec = system.dispatcher
    system.log.info(s"Node terminated with $conf on port: $port for $actorSystem.")
    Await.result(system.terminate(), 3 seconds)
  }
}