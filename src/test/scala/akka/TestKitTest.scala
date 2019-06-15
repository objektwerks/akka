package akka

import akka.actor._
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.language.postfixOps

class Ping extends Actor {
  def receive: Receive = {
    case ping: String => sender ! ping
  }
}

class TestKitTest extends TestKit(ActorSystem("testkit", Conf.config))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {
  val ping: ActorRef = system.actorOf(Props[Ping], name = "ping")

  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  "Ping actor" should {
    "reply with a ping" in {
      within(1 second) {
        ping ! "ping"
        expectMsg("ping")
      }
    }
  }
}