package cluster

import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._

abstract class Node extends App {
  implicit val timeout = Timeout(10 seconds)

  if (args.length < 3) {
    println("Please, provide a (1) port; (2) role; and (3) conf file name.")
    System.exit(-1)
  }

  val port = args(0).toInt
  val role = args(1)
  val conf = args(2)
  println(s"Loading conf: $conf on port: $port for role: $role.")

  val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port = $port").
      withFallback(ConfigFactory.parseString(s"akka.cluster.roles = [$role]")).
      withFallback(ConfigFactory.load(conf))

  val system = ActorSystem.create(role, config)
  system.log.info(s"Node initialized with conf: $config on port: $port for role: $role!")

  sys.addShutdownHook {
    implicit val ec = system.dispatcher
    system.log.info(s"Node with conf: $config on port: $port for role: $role terminating...")
    Await.result(system.terminate(), 3 seconds)
  }
}