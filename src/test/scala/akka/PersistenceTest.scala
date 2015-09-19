package akka

import java.util.concurrent.TimeUnit

import akka.actor.{ActorLogging, ActorRef, ActorSystem, Props}
import akka.pattern._
import akka.persistence.{Recovery, PersistentActor, SnapshotOffer}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}

case class ComputeCommand(number: Int)
case object SnapshotCommand

case class ComputedEvent(number: Int)

case class ComputedState(computedEvents: List[ComputedEvent] = Nil) {
  def addComputedEvent(computedEvent: ComputedEvent): ComputedState = copy(computedEvent :: computedEvents)
}

class Computer extends PersistentActor with ActorLogging {
  log.info("*** Computer persistent actor initialized.")
  override def persistenceId: String = "computer-persistence-id"

  var computedState = ComputedState()

  def updateComputedState(computedEvent: ComputedEvent): Unit = {
    log.info("*** Updating computed state.")
    computedState = computedState.addComputedEvent(computedEvent)
  }

  // WARNING: Commands are NOT received!!!
  override def receiveCommand: Receive = {
    case ComputeCommand(number) =>
      log.info("*** Received ComputeCommand.")
      persist(ComputedEvent(number)) { event =>
        updateComputedState(event)
        context.system.eventStream.publish(event)
        sender ! computedState.computedEvents.size
      }
    case SnapshotCommand =>
      log.info("*** Received SnapshotCommand.")
      saveSnapshot(computedState)
  }

  override def receiveRecover: Receive = {
    case computedEvent: ComputedEvent =>
      log.info("*** Recovered ComputeEvent.")
      updateComputedState(computedEvent)
    case SnapshotOffer(_, snapshot: ComputedState) =>
      log.info("*** Recovered snapshot of ComputedState.")
      computedState = snapshot
  }
}

class PersistenceTest extends FunSuite with BeforeAndAfterAll {
  val log = LoggerFactory.getLogger(classOf[PersistenceTest])
  implicit val ec = ExecutionContext.global
  implicit val timeout = new Timeout(3, TimeUnit.SECONDS)
  val system: ActorSystem = ActorSystem.create("persistence")
  val computer: ActorRef = system.actorOf(Props[Computer], name = "computer")

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 3 seconds)
  }

  test("command") {
    val future = computer ? ComputeCommand(1)
    future onComplete {
      case Success(count) => assert(count == 1)
      case Failure(failure) => log.error(failure.getMessage); throw failure
    }
    computer ! SnapshotCommand
  }
}