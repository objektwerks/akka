package akka

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.pattern._
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.{global => ec}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

sealed trait KindOf
case object Tell extends KindOf
case object TellWorker extends KindOf
case object Ask extends KindOf
case object AskWorker extends KindOf
case object AbortWorker extends KindOf
case class Message(kindOf: KindOf, from: String, message: String)

class Master extends Actor with ActorLogging {
  log.info(s"Master created: $self")
  private implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  private val worker: ActorRef = context.actorOf(Props[Service], name = "worker")

  def receive = {
    case Message(Tell, from, message) => log.info(s"\nMaster received $message from $from.")
    case Message(TellWorker, from, message) => worker ! Message(Tell, s"$from -> Master", message)
    case Message(Ask, from, message) => sender ! s"Master received and responded to $message from $from."
    case Message(AskWorker, from, message) =>
      // Test passes, but occasionally throws an AskTimeoutException.
      worker ? Message(AskWorker, s"$from -> Master", message) pipeTo sender
    case Message(AbortWorker, from, message) => worker ! Message(AbortWorker, s"$from -> Master", message)
    case _ => log.info("Master received an invalid message.")
  }
}

class Worker extends Actor with ActorLogging {
  log.info(s"Worker created: $self")
  log.info(s"Worker parent: ${context.parent.path.name}")

  def receive = {
    case Message(Tell, from, message) => log.info(s"Worker received $message from $from.")
    case Message(AskWorker, from, message) => sender ! s"Worker received and responded to $message from $from."
    case Message(AbortWorker, from, message) => throw new Exception(message)
    case _ => log.info("Worker received an invalid message.")
  }
}

class Identifier extends Actor with ActorLogging {
  def receive = {
    case path: String => context.actorSelection(path) ! Identify(path)
    case ActorIdentity(path, Some(ref)) => log.info(s"Actor identified: $ref at path: $path.")
    case ActorIdentity(path, None) => log.info(s"Actor NOT identified at path: $path.")
  }
}

class TellAskTest extends FunSuite with BeforeAndAfterAll {
  private val log = LoggerFactory.getLogger(classOf[TellAskTest])
  private implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  private val system: ActorSystem = ActorSystem.create("funky")
  private val master: ActorRef = system.actorOf(Props[Master], name = "master")
  private val identifier: ActorRef = system.actorOf(Props[Identifier], name = "identifier")

  override protected def afterAll(): Unit = {
    system.shutdown
    system.awaitTermination(3 seconds)
  }

  test("actor selection") {
    identifier ! "/user/*"
    identifier ! "/user/master/*"
    identifier ! "/funky/*"
  }

  test("system ! master") {
    master ! Message(Tell, "System", "tell ! message")
  }

  test("system ! master ! worker") {
    master ! Message(TellWorker, "System", "tell ! message")
  }

  test("system ? master") {
    val future = master ? Message(Ask, "System", "ask ? message")
    future onComplete {
      case Success(message) => assert(message.toString.nonEmpty); log.info(message.toString)
      case Failure(failure) => log.info(failure.getMessage); throw failure
    }
  }

  // Test passes, but occasionally throws an AskTimeoutException.
  test("system ? master ? worker") {
    val future = master ? Message(AskWorker, "System", "ask ? message")
    future onComplete  {
      case Success(message) => assert(message.toString.nonEmpty);  log.info(message.toString)
      case Failure(failure) => log.info(failure.getMessage); throw failure
    }
  }

  test("system ! master ! abort worker") {
    master ! Message(AbortWorker, "System", "abort ! message")
    master ! Message(TellWorker, "System", "AFTER ABORT tell ! message")
  }
}