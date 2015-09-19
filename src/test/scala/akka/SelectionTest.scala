package akka

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.pattern._
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.{global => ec}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

case object ToGrandParents
case object ToParents
case object ToChildren

class GrandParents extends Actor with ActorLogging {
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
  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  val system: ActorSystem = ActorSystem.create("selection")
  val grandparents = system.actorOf(Props[GrandParents], name = "grandparents")

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 3 seconds)
  }

  test("grand parents") {
    (system.actorSelection("/user/grandparents") ? ToGrandParents) onComplete {
      case Success(message) => assert(message == "grandparents")
      case Failure(failure) => throw failure
    }
    (system.actorSelection("/user/grandparents") ? ToParents) onComplete {
      case Success(message) => assert(message == "parents")
      case Failure(failure) => throw failure
    }
    (system.actorSelection("/user/*") ? ToChildren) onComplete {
      case Success(message) => assert(message == "children")
      case Failure(failure) => throw failure
    }
  }

  test("parents") {
    (system.actorSelection("/user/grandparents/parents") ? ToParents) onComplete {
      case Success(message) => assert(message == "parents")
      case Failure(failure) => throw failure
    }
    (system.actorSelection("/user/grandparents/*") ? ToChildren) onComplete {
      case Success(message) => assert(message == "children")
      case Failure(failure) => throw failure
    }
  }

  test("children") {
    (system.actorSelection("/user/grandparents/parents/*") ? ToChildren) onComplete {
      case Success(message) => assert(message == "children")
      case Failure(failure) => throw failure
    }
  }
}