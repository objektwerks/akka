package akka

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.ExecutionContext.Implicits.{global => ec}
import scala.concurrent.duration._

class Identifier extends Actor with ActorLogging {
  def receive = {
    case path: String => context.actorSelection(path) ! Identify(path)
    case ActorIdentity(path, Some(ref)) => log.info(s"Actor identified: $ref at path: $path.")
    case ActorIdentity(path, None) => log.info(s"Actor NOT identified at path: $path.")
  }
}

class SelectionTest extends FunSuite with BeforeAndAfterAll {
  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  val system: ActorSystem = ActorSystem.create("funky")
  val identifier: ActorRef = system.actorOf(Props[Identifier], name = "identifier")

  override protected def afterAll(): Unit = {
    system.shutdown()
    system.awaitTermination(3 seconds)
  }

  test("selection") {
    identifier ! "/user/*"
    identifier ! "/user/master/*"
    identifier ! "/funky/*"
  }
}