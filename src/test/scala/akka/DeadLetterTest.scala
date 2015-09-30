package akka

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.Await
import scala.concurrent.duration._

class Service extends Actor with ActorLogging {
  def receive = {
    case message: String => log.info(s"Service: $message")
  }
}

class Listener extends Actor with ActorLogging {
  def receive = {
    case deadLetter: DeadLetter => log.info(s"Deadletter: ${deadLetter.message}")
  }
}

class DeadLetterTest extends FunSuite  with BeforeAndAfterAll {
  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  val system: ActorSystem = ActorSystem.create("deadletter", Conf.config)
  val service: ActorRef = system.actorOf(Props[Service], name = "service")
  val listener: ActorRef = system.actorOf(Props[Listener], name = "listener")
  system.eventStream.subscribe(listener, classOf[DeadLetter])

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 1 second)
  }

  test("dead letter") {
    service ! "First message!"
    Thread.sleep(500)
    service ! PoisonPill
    Thread.sleep(500)
    service ! "Second message!"
  }
}