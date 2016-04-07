package cluster

import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._

object SeedNode extends App {
  implicit val timeout = Timeout(10 seconds)
  val port = args(0).toInt
  val role = args(1)
  val conf = ConfigFactory.parseString(s"akka.remote.netty.tcp.port = $port").
    withFallback(ConfigFactory.parseString(s"akka.cluster.roles = [$role]")).
    withFallback(ConfigFactory.load("seed-node-akka.conf"))
  val system = ActorSystem.create(role, conf)
  system.log.info(s"Seed Node initialized on port: $port for role: $role!")
  sys.addShutdownHook {
    implicit val ec = system.dispatcher
    system.log.info(s"Seed Node on port: $port for role: $role terminating...")
    Await.result(system.terminate(), 3 seconds)
  }
}