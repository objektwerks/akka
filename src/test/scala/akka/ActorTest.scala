package akka

import akka.actor._
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.language.postfixOps

class Echo extends Actor {
  def receive: Receive = {
    case echo: String => sender ! echo
  }
}

class ActorTest extends TestKit(ActorSystem("testkit", Conf.config))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {
  val echo: ActorRef = system.actorOf(Props[Echo], name = "echo")

  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  "Echo actor" should {
    "reply with a echo" in {
      within(1 second) {
        echo ! "ping"
        expectMsg("ping")
      }
    }
  }

  "Echo actor" should {
    "reply with a pong" in {
      val probe = TestProbe("probe")
      within(1 second) {
        probe.send(echo, "pong")
        probe.expectMsg("pong")
      }
    }
  }
}