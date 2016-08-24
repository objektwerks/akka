package brewery

import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._

class EmbeddedSeedNode(app: String, conf: String) {
  implicit private val timeout = Timeout(10 seconds)
  private val config = ConfigFactory.load(conf)
  private val system = ActorSystem.create(app, config)

  def terminate(): Unit = {
    implicit val ec = system.dispatcher
    Await.result(system.terminate(), 3 seconds)
  }
}