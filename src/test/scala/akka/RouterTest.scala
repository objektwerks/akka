package akka

import java.time.LocalTime

import akka.actor._
import akka.pattern._
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

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

class Time extends Actor with ActorLogging {
  def receive = {
    case timeIs: String =>
      val time = s"$timeIs ${LocalTime.now.toString}"
      log.info(s"*** $time")
      sender.tell(time, context.parent)
  }
}

class RouterTest extends FunSuite with BeforeAndAfterAll {
  implicit val timeout = Timeout(1 second)
  val system: ActorSystem = ActorSystem.create("router", Conf.config)
  val clock: ActorRef = system.actorOf(Props[Clock], name = "clock")

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 1 second)
    ()
  }

  test("router") {
    val whatTimeIsIt = (i: Int) => {
      val future = ask(clock, s"$i. The time is:").mapTo[String]
      val time = Await.result(future, 1 second)
      assert(time.nonEmpty)
    }
    for(i <- 1 to 3) whatTimeIsIt(i)
  }
}