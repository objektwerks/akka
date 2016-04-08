package cluster

import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._

class EmbeddedSeedNode(port: Int, role: String) {
  implicit val timeout = Timeout(10 seconds)
  val conf = ConfigFactory.parseString(s"akka.remote.netty.tcp.port = $port").
    withFallback(ConfigFactory.parseString(s"akka.cluster.roles = [$role]")).
    withFallback(ConfigFactory.load("seed-node.conf"))
  val system = ActorSystem.create(role, conf)
  system.log.info(s"Embedded Seed Node initialized on port: $port for role: $role!")

  def terminate(): Unit = {
    implicit val ec = system.dispatcher
    system.log.info(s"Embedded Seed Node on port: $port for role: $role terminating...")
    Await.result(system.terminate(), 3 seconds)
  }
}