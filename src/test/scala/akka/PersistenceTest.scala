package akka

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

import akka.actor.{ActorLogging, ActorRef, ActorSystem, Props}
import akka.pattern._
import akka.persistence.{SaveSnapshotSuccess, PersistentActor, SaveSnapshotFailure, SnapshotOffer}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}

abstract class Command[T](f: (T) => T, n: T) {
  def execute: T = f(n)
}
case class ComputeCommand(f: (Int) => Int, n: Int) extends Command(f, n)

abstract class Event[T](value: T, created: LocalDateTime = LocalDateTime.now())
case class ComputedEvent(value: Int) extends Event(value)

case class ComputedState(computedEvents: List[ComputedEvent] = Nil) {
  def addComputedEvent(computedEvent: ComputedEvent): ComputedState = copy(computedEvent :: computedEvents)
}

case object State
case object Snapshot
case object Shutdown

class Computer extends PersistentActor with ActorLogging {
  override def persistenceId: String = "computer-persistence-id"

  var state = ComputedState()

  def updateState(computedEvent: ComputedEvent): Unit = {
    state = state.addComputedEvent(computedEvent)
  }

  override def receiveCommand: Receive = {
    case command: ComputeCommand =>
      persist(ComputedEvent(command.execute)) { event =>
        updateState(event)
        context.system.eventStream.publish(event)
      }
    case State => sender ! state.computedEvents.size
    case Snapshot => saveSnapshot(state)
    case SaveSnapshotSuccess(metadata) => log.info(s"Computer snapshot successful: $metadata")
    case SaveSnapshotFailure(metadata, reason) => throw reason
    case Shutdown => context.stop(self)
  }

  override def receiveRecover: Receive = {
    case computedEvent: ComputedEvent => updateState(computedEvent)
    case SnapshotOffer(_, snapshot: ComputedState) => state = snapshot
  }
}

class PersistenceTest extends FunSuite with BeforeAndAfterAll {
  implicit val ec = ExecutionContext.global
  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  val system: ActorSystem = ActorSystem.create("persistence")
  val computer: ActorRef = system.actorOf(Props[Computer], name = "computer")

  def fibonacci(n: Int): Int = {
    @tailrec
    def loop(n: Int, a: Int, b: Int): Int = n match {
      case 0 => a
      case _ => loop(n - 1, b, a + b)
    }
    loop(n, 0, 1)
  }

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 3 seconds)
  }

  test("persistent") {
    val command = ComputeCommand(fibonacci, 1)
    assert(command.execute == 1)

    val event = ComputedEvent(command.execute)
    assert(event.value == 1)

    for (n <- 1 to 100) {
      computer ! ComputeCommand(fibonacci, n)
      if (n % 25 == 0) computer ! Snapshot
    }

    val state = computer ? State
    state onComplete {
      case Success(count) => assert(count == 100)
      case Failure(failure) => throw failure
    }
    Await.result(state, 1 second)

    computer ! Shutdown
  }
}