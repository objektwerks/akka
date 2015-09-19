package akka

import java.util.concurrent.TimeUnit

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor._
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.{global => ec}
import scala.concurrent.duration._

trait Task
case object Play extends Task
case object CleanRoom extends Task
case object CleanWindows extends Task
case object CleanBathroom extends Task
case object StandInCorner extends Task

class CleanRoomException(cause: String) extends Exception(cause)
class CleanWindowsException(cause: String) extends Exception(cause)
class CleanBathroomException(cause: String) extends Exception(cause)
class StandInCornerException(cause: String) extends Exception(cause)

class Nanny extends Actor with ActorLogging {
  log.info(s"Nanny created: $self")
  private implicit val timeout = new Timeout(3, TimeUnit.SECONDS)
  private val child: ActorRef = context.actorOf(Props[Child], name = "child")

  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
    case _: CleanRoomException => Restart
    case _: CleanWindowsException => Restart
    case _: CleanBathroomException => Restart
    case _: StandInCornerException => Stop
  }

  def receive = {
    case Play => child ! Play
    case CleanRoom => child ! CleanRoom
    case CleanWindows => child ! CleanWindows
    case CleanBathroom => child ! CleanBathroom
    case StandInCorner => child ! StandInCorner
    case _ => log.info("Nanny received an invalid message.")
  }
}

class Child extends Actor with ActorLogging {
  log.info(s"Child created: $self")

  def receive = {
    case Play => log.info("Child happily wishes to play!")
    case CleanRoom => throw new CleanRoomException("Child refuses to clean room!")
    case CleanWindows => throw new CleanWindowsException("Child refuses to clean windows!")
    case CleanBathroom => throw new CleanBathroomException("Child refuses to clean bathroom!")
    case StandInCorner => throw new StandInCornerException("Child refuses to stand in corner!")
    case _ => log.info("Child received an invalid message.")
  }
}

class Watcher extends Actor with ActorLogging {
  private implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  private val futureChild = context.system.actorSelection("/user/nanny/*").resolveOne()
  futureChild onSuccess { case child => context.watch(child)}

  def receive = {
    case Terminated(child) => log.info(s"Watcher terminated event: ${child.path.name} TERMINATED!")
  }
}

class SupervisorStrategyTest extends FunSuite with BeforeAndAfterAll {
  private implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  private val system: ActorSystem = ActorSystem.create("supervisor")
  private val nanny: ActorRef = system.actorOf(Props[Nanny], name = "nanny")
  system.actorOf(Props[Time], name = "watcher")

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 3 seconds)
  }

  test("nanny ! child") {
    nanny ! Play
    Thread.sleep(1000)
    nanny ! CleanRoom
    Thread.sleep(1000)
    nanny ! CleanWindows
    Thread.sleep(1000)
    nanny ! CleanBathroom
    Thread.sleep(1000)
    nanny ! StandInCorner
  }
}