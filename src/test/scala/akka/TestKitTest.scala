package akka

import akka.actor._
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._

class Ping extends Actor with ActorLogging {
  def receive = {
    case ping: String => log.info(ping); sender ! ping
    case _ => log.info("Ping received an invalid message.")
  }
}

class TestKitTest extends TestKit(ActorSystem("testkit", Conf.config)) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {
  val ping: ActorRef = system.actorOf(Props[Ping], name = "ping")

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 1 second)
  }

  "Ping actor" should {
    "reply with an identical message" in {
      within(2 seconds) {
        ping ! "ping"
        expectMsg("ping")
      }
    }
  }
}