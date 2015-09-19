package akka

import java.time.LocalTime
import java.util.concurrent.TimeUnit

import akka.actor._
import akka.pattern._
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.{global => ec}
import scala.concurrent.duration._

class Clock extends Actor {
  var router = {
    val routees = Vector.fill(3) {
      val t = context.actorOf(Props[Time])
      context watch t
      ActorRefRoutee(t)
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
  val system: ActorSystem = ActorSystem.create("router")
  val clock: ActorRef = system.actorOf(Props[Clock], name = "clock")

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 3 seconds)
  }

  test("router") {
    clock ? "time a: " onSuccess { case t: String => assert(t.nonEmpty) }
    clock ? "time b: " onSuccess { case t: String => assert(t.nonEmpty) }
    clock ? "time c: " onSuccess { case t: String => assert(t.nonEmpty) }
  }
}