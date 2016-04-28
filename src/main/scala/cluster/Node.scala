package cluster

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import kamon.Kamon

import scala.concurrent.Await
import scala.concurrent.duration._

abstract class Node extends App {
  implicit val timeout = Timeout(10 seconds)

  if (args.length < 3) {
    println("Please, provide a (1) conf; (2) port; and (3) actor system name.")
    System.exit(-1)
  }

  val conf = args(0)
  val port = args(1).toInt
  val actorSystemName = args(2)
  println(s"Loading conf: $conf on port: $port for actor system: $actorSystemName.")

  val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port = $port").withFallback(ConfigFactory.load(conf))

  val system = ActorSystem.create(actorSystemName, config)
  system.actorOf(Props[ClusterListener], name = "cluster-listener")
  println(s"Node initialized with $conf on port: $port for actor system: $actorSystemName!")

  Kamon.start()

  sys.addShutdownHook {
    implicit val ec = system.dispatcher
    Kamon.shutdown()
    println(s"Node terminated with $conf on port: $port for actor system: $actorSystemName.")
    Await.result(system.terminate(), 3 seconds)
  }
}