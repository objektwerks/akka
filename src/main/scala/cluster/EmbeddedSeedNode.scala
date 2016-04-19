package cluster

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._

class EmbeddedSeedNode(conf: String, port: Int, actorSystem: String) {
  implicit val timeout = Timeout(10 seconds)

  val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port = $port").withFallback(ConfigFactory.load(conf))

  val system = ActorSystem.create(actorSystem, config)
  system.actorOf(Props[ClusterListener], name = "cluster-listener")
  system.log.info(s"Embedded Seed Node initialized with $conf on port: $port for $actorSystem!")

  def terminate(): Unit = {
    implicit val ec = system.dispatcher
    system.log.info(s"Embedded Seed Node terminated with $conf on port: $port for $actorSystem.")
    Await.result(system.terminate(), 3 seconds)
  }
}