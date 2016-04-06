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

  log.info(s"Master created: $self")
  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  val worker: ActorRef = context.actorOf(Props[Worker], name = "worker")

  def receive = {
    case Message(Tell, from, message) => log.info(s"Master received $message from $from.")
    case Message(TellWorker, from, message) => worker ! Message(Tell, s"$from -> Master", message)
    case Message(Ask, from, message) => sender ! s"Master received and responded to $message from $from."
    case Message(AskWorker, from, message) => worker ? Message(AskWorker, s"$from -> Master", message) pipeTo sender
    case _ => log.info("Master received an invalid message.")
  }
}

class Worker extends Actor with ActorLogging {
  log.info(s"Worker created: $self")
  log.info(s"Worker parent: ${context.parent.path.name}")

  def receive = {
    case Message(Tell, from, message) => log.info(s"Worker received $message from $from.")
    case Message(AskWorker, from, message) => sender ! s"Worker received and responded to $message from $from."
    case _ => log.info("Worker received an invalid message.")
  }
}

class TellAskTest extends FunSuite with BeforeAndAfterAll {
  implicit val timeout = Timeout(1 second)
  val system: ActorSystem = ActorSystem.create("tellask", Conf.config)
  val master: ActorRef = system.actorOf(Props[Master], name = "master")

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 1 second)
  }

  test("simulation.system ! master") {
    master ! Message(Tell, "System", "tell ! message")
  }

  test("simulation.system ! master ! worker") {
    master ! Message(TellWorker, "System", "tell ! message")
  }

  test("simulation.system ? master") {
    assert(Await.result(master ? Message(Ask, "System", "ask ? message"), 1 second).asInstanceOf[String].nonEmpty)
    val future = ask(master, Message(Ask, "System", "ask ? message")).mapTo[String]
    assert(Await.result(future, 1 second).nonEmpty)
  }

  test("simulation.system ? master ? worker") {
    assert(Await.result(master ? Message(AskWorker, "System", "ask ? message"), 1 second).asInstanceOf[String].nonEmpty)
    val future = ask(master, Message(AskWorker, "System", "ask ? message")).mapTo[String]
    assert(Await.result(future, 1 second).nonEmpty)
  }
}