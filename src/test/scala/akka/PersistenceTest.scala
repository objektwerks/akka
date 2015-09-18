package akka

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.persistence.{SnapshotOffer, PersistentActor}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration._

case class ComputeCommand(number: Int)

case class ComputeEvent(number: Int)

case class ComputeState(events: List[Int] = Nil) {
  def updated(event: ComputeEvent): ComputeState = copy(event.number :: events)

  def size: Int = events.size

  override def toString: String = events.reverse.toString()
}

class Computer extends PersistentActor {
  override def persistenceId: String = "1"

  var state = ComputeState()

  def updateState(event: ComputeEvent): Unit = state = state.updated(event)

  override def receiveCommand: Receive = {
    case event: ComputeEvent => updateState(event)
    case SnapshotOffer(_, snapshot: ComputeState) => state = snapshot
  }

  override def receiveRecover: Receive = {
    case ComputeCommand(number) =>
      persist(ComputeEvent(number))(updateState)
      persist(ComputeEvent(number)) { event =>
        updateState(event)
        context.system.eventStream.publish(event)
      }
    case "snapshot"  => saveSnapshot(state)
    case "state" => state
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