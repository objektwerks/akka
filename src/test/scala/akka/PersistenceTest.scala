package akka

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.persistence.{SnapshotOffer, PersistentActor}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration._

case object SnapshotCommand
case class ComputeCommand(number: Int)

case class ComputedEvent(number: Int)

case class ComputedState(computedEvents: List[ComputedEvent] = Nil) {
  def addComputedEvent(computedEvent: ComputedEvent): ComputedState = copy(computedEvent :: computedEvents)
}

class Computer extends PersistentActor {
  override def persistenceId: String = "1"

  var computedState = ComputedState()

  def updateComputedState(computedEvent: ComputedEvent): Unit = computedState = computedState.addComputedEvent(computedEvent)

  override def receiveCommand: Receive = {
    case computedEvent: ComputedEvent => updateComputedState(computedEvent)
    case SnapshotOffer(_, snapshot: ComputedState) => computedState = snapshot
  }

  override def receiveRecover: Receive = {
    case ComputeCommand(number) =>
      persist(ComputedEvent(number))(updateComputedState)
      persist(ComputedEvent(number)) { computedEvent =>
        updateComputedState(computedEvent)
        context.system.eventStream.publish(computedEvent)
      }
    case SnapshotCommand => saveSnapshot(computedState)
  }
}

class PersistenceTest extends FunSuite with BeforeAndAfterAll {
  val log = LoggerFactory.getLogger(classOf[PersistenceTest])
  implicit val timeout = new Timeout(3, TimeUnit.SECONDS)
  val system: ActorSystem = ActorSystem.create("persistence")
  val computer: ActorRef = system.actorOf(Props[Computer], name = "compute")

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 3 seconds)
  }

  test("actor") {

  }
}