package akka

import akka.actor._
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._

class Ping extends Actor with ActorLogging {
  def receive = {
    case ping: String => sender ! ping
  }
}

class TestKitTest extends TestKit(ActorSystem("testkit", Conf.config))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {
  val ping: ActorRef = system.actorOf(Props[Ping], name = "ping")

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 1 second)
  }

  "Ping actor" should {
    "reply with a ping" in {
      within(2 seconds) {
        ping ! "ping"
        expectMsg("ping")
      }
    }
  }
}