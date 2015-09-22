package akka

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.pattern._
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

case object ToGrandParents
case object ToParents
case object ToChildren

class GrandParents extends Actor with ActorLogging {
  import context.dispatcher

  log.info(s"GrandParents created: $self")
  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  val parent: ActorRef = context.actorOf(Props[Parents], name = "parents")

  def receive = {
    case ToGrandParents => sender ! "grandparents"
    case ToParents => parent ? ToParents pipeTo sender
    case ToChildren => parent ? ToChildren pipeTo sender
  }
}

class Parents extends Actor with ActorLogging {
  import context.dispatcher

  log.info(s"Parents created: $self")
  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  val child: ActorRef = context.actorOf(Props[Children], name = "children")

  def receive = {
    case ToParents => sender ! "parents"
    case ToChildren => child ? ToChildren pipeTo sender
  }
}

class Children extends Actor with ActorLogging {
  log.info(s"Children created: $self")
  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)

  def receive = {
    case ToChildren => sender ! "children"
  }
}

class SelectionTest extends FunSuite with BeforeAndAfterAll {
  implicit val ec = ExecutionContext.global
  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  val system: ActorSystem = ActorSystem.create("selection")
  val grandparents: ActorRef = system.actorOf(Props[GrandParents], name = "grandparents")

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 1 second)
  }

  test("grand parents") {
    assert("grandparents" == Await.result(system.actorSelection("/user/grandparents") ? ToGrandParents, 1 second).asInstanceOf[String])
    assert("parents" == Await.result(system.actorSelection("/user/grandparents") ? ToParents, 1 second).asInstanceOf[String])
    assert("children" == Await.result(system.actorSelection("/user/grandparents") ? ToChildren, 1 second).asInstanceOf[String])
  }

  test("parents") {
    assert("parents" == Await.result(system.actorSelection("/user/grandparents/parents") ? ToParents, 1 second).asInstanceOf[String])
    assert("children" == Await.result(system.actorSelection("/user/grandparents/*") ? ToChildren, 1 second).asInstanceOf[String])
  }

  test("children") {
    assert("children" == Await.result(system.actorSelection("/user/grandparents/parents/*") ? ToChildren, 1 second).asInstanceOf[String])
  }
}