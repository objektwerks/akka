package cluster

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

class SeedNode(port: Int, role: String) {
  implicit val timeout = Timeout(10 seconds)
  val conf = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
    withFallback(ConfigFactory.parseString(s"akka.cluster.roles = [$role]")).
    withFallback(ConfigFactory.load("seed-node-akka.conf"))
  val system = ActorSystem.create("brewery", conf)
  system.actorOf(Props[Listener], name = "listener")
  system.log.info(s"Seed Node initialized on port: $port for role: $role!")
}