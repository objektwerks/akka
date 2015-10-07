package akka

import java.time.LocalTime
import java.util.concurrent.TimeUnit

import akka.actor._
import akka.pattern._
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.Await
import scala.concurrent.duration._

class Clock extends Actor {
  var router = {
    val routees = Vector.fill(3) {
      val time = context.actorOf(Props[Time])
      context watch time
      ActorRefRoutee(time)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case m: String => router.route(m, sender())
  }
}

class Time extends Actor {
  def receive = {
    case m: String => sender.tell(s"$m" + LocalTime.now().toString, context.parent)
  }
}

class RouterTest extends FunSuite with BeforeAndAfterAll {
  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  val system: ActorSystem = ActorSystem.create("router", Conf.config)
  val clock: ActorRef = system.actorOf(Props[Clock], name = "clock")

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 1 second)
  }

  test("router") {
    assert(Await.result(clock ? "time a: ", 1 second).asInstanceOf[String].nonEmpty)
    assert(Await.result(clock ? "time b: ", 1 second).asInstanceOf[String].nonEmpty)
    assert(Await.result(clock ? "time c: ", 1 second).asInstanceOf[String].nonEmpty)
  }
}