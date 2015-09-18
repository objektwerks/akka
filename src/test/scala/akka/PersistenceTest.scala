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

case class ComputeEvent(number: Int)

case class ComputeState(computeEvents: List[ComputeEvent] = Nil) {
  def addComputeEvent(computeEvent: ComputeEvent): ComputeState = copy(computeEvent :: computeEvents)
}

class Computer extends PersistentActor {
  override def persistenceId: String = "1"

  var computeState = ComputeState()

  def updateComputeState(computeEvent: ComputeEvent): Unit = computeState = computeState.addComputeEvent(computeEvent)

  override def receiveCommand: Receive = {
    case computeEvent: ComputeEvent => updateComputeState(computeEvent)
    case SnapshotOffer(_, snapshot: ComputeState) => computeState = snapshot
  }

  override def receiveRecover: Receive = {
    case ComputeCommand(number) =>
      persist(ComputeEvent(number))(updateComputeState)
      persist(ComputeEvent(number)) { computeEvent =>
        updateComputeState(computeEvent)
        context.system.eventStream.publish(computeEvent)
      }
    case SnapshotCommand  => saveSnapshot(computeState)
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