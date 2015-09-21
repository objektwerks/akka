package akka

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern._
import akka.persistence.{PersistentActor, SnapshotOffer}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}

abstract class Command[T](f: (T) => T, x: T) {
  def execute: T = f(x)
}
case class ComputeCommand(f: (Int) => Int, x: Int) extends Command(f, x)

abstract class Event[T](value: T, created: LocalDateTime = LocalDateTime.now())
case class ComputedEvent(value: Int) extends Event(value)

case class ComputedState(computedEvents: List[ComputedEvent] = Nil) {
  def addComputedEvent(computedEvent: ComputedEvent): ComputedState = copy(computedEvent :: computedEvents)
}

case object State
case object Snapshot
case object Shutdown

class Computer extends PersistentActor {
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

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 3 seconds)
  }

  test("persistent") {
    val command = ComputeCommand((x: Int) => x * x, 2)
    assert(command.execute == 4)

    val event = ComputedEvent(command.execute)
    assert(event.value == 4)

    computer ! ComputeCommand((x: Int) => x * x, 3)
    computer ! Snapshot

    val future = computer ? State
    future onComplete {
      case Success(state) => assert(state == 1)
      case Failure(failure) => throw failure
    }
    Await.result(future, 1 second)

    computer ! Shutdown
  }
}