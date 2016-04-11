package akka

import java.time.LocalTime

import akka.actor._
import akka.pattern._
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.Await
import scala.concurrent.duration._

class Clock extends Actor {
  var router = {
    val routees = Vector.fill(2) {
      val time = context.actorOf(Props[Time])
      context watch time
      ActorRefRoutee(time)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case timeIs: String => router.route(timeIs, sender)
  }
}

class Time extends Actor {
  def receive = {
    case timeIs: String => sender.tell(s"$timeIs ${LocalTime.now().toString}", context.parent)
  }
}

class RouterTest extends FunSuite with BeforeAndAfterAll {
  implicit val timeout = Timeout(1 second)
  val system: ActorSystem = ActorSystem.create("router", Conf.config)
  val clock: ActorRef = system.actorOf(Props[Clock], name = "clock")

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 1 second)
  }

  test("router") {
    for(i <- 1 to 30) {
      whatTimeIsIt()
    }
  }

  def whatTimeIsIt(): Unit = {
    val future = ask(clock, s"time is:").mapTo[String]
    assert(Await.result(future, 1 second).nonEmpty)
  }
}