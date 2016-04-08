package cluster

import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._

class EmbeddedSeedNode(port: Int, role: String, conf: String) {
  implicit val timeout = Timeout(10 seconds)
  val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port = $port").withFallback(ConfigFactory.load(conf))
  val system = ActorSystem.create(role, config)
  system.log.info(s"Embedded Seed Node initialized with conf: $conf on port: $port for role: $role!")

  def terminate(): Unit = {
    implicit val ec = system.dispatcher
    system.log.info(s"Embedded Seed Node terminated with conf: $conf on port: $port for role: $role.")
    Await.result(system.terminate(), 3 seconds)
  }
}