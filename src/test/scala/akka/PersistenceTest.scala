package akka

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern._
import akka.persistence.{PersistentActor, SnapshotOffer}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}

case class ComputeCommand(number: Int)

case class ComputedEvent(number: Int)

case class ComputedState(computedEvents: List[ComputedEvent] = Nil) {
  def addComputedEvent(computedEvent: ComputedEvent): ComputedState = copy(computedEvent :: computedEvents)
}

case object Snapshot

class Computer extends PersistentActor {
  override def persistenceId: String = "computer-persistence-id"

  var computedState = ComputedState()

  def updateComputedState(computedEvent: ComputedEvent): Unit = {
    computedState = computedState.addComputedEvent(computedEvent)
  }

  override def receiveCommand: Receive = {
    case ComputeCommand(number) =>
      persist(ComputedEvent(number)) { event =>
        updateComputedState(event)
        context.system.eventStream.publish(event)
        sender ! computedState.computedEvents.size
      }
    case Snapshot => saveSnapshot(computedState)
  }

  override def receiveRecover: Receive = {
    case computedEvent: ComputedEvent => updateComputedState(computedEvent)
    case SnapshotOffer(_, snapshot: ComputedState) => computedState = snapshot
  }
}

class PersistenceTest extends FunSuite with BeforeAndAfterAll {
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
      case Failure(failure) => throw failure
    }
    Await.result(future, 3 seconds)
    computer ! Snapshot
  }
}