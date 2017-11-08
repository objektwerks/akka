package akka

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.pattern._
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.Await
import scala.concurrent.duration._

sealed trait MessageType
case object Tell extends MessageType
case object TellWorker extends MessageType
case object Ask extends MessageType
case object AskWorker extends MessageType

case class Message(messageType: MessageType, from: String, message: String)

class Master extends Actor with ActorLogging {
  import context.dispatcher

  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  val worker: ActorRef = context.actorOf(Props[Worker], name = "worker")

  def receive = {
    case Message(Tell, from, message) => log.info(s"*** Master received $message from $from.")
    case Message(TellWorker, from, message) => worker ! Message(Tell, s"$from -> Master", message)
    case Message(Ask, from, message) => sender ! s"*** Master received and responded to $message from $from."
    case Message(AskWorker, from, message) => worker ? Message(AskWorker, s"$from -> Master", message) pipeTo sender
  }
}

class Worker extends Actor with ActorLogging {
  def receive = {
    case Message(Tell, from, message) => log.info(s"*** Worker received $message from $from.")
    case Message(AskWorker, from, message) => sender ! s"Worker received and responded to $message from $from."
  }
}

class TellAskTest extends FunSuite with BeforeAndAfterAll {
  implicit val timeout = Timeout(1 second)
  val system: ActorSystem = ActorSystem.create("tellask", Conf.config)
  val master: ActorRef = system.actorOf(Props[Master], name = "master")

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 1 second)
  }

  test("master ! tell") {
    master ! Message(Tell, "System", "tell ! message")
  }

  test("master ! tell worker") {
    master ! Message(TellWorker, "System", "tell ! message")
  }

  test("master ? ask") {
    assert(Await.result((master ? Message(Ask, "System", "ask ? message")).mapTo[String], 1 second).nonEmpty)
  }

  test("master ? ask worker") {
    assert(Await.result((master ? Message(AskWorker, "System", "ask ? message")).mapTo[String], 1 second).nonEmpty)
  }
}