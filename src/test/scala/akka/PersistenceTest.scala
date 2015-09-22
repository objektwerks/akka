package akka

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

import akka.actor.{ActorLogging, ActorRef, ActorSystem, Props}
import akka.pattern._
import akka.persistence._
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

case class Compute(f: (Int) => Int, n: Int) {
  def execute: Int = f(n)
}

case class Computed(value: Int, created: LocalDateTime = LocalDateTime.now())

case class Events(events: List[Computed] = Nil) {
  def add(event: Computed): Events = copy(event :: events)
  def list: List[Int] = events.map(c => c.value)
}

case object Result
case object Snapshot
case object Shutdown

class Computer extends PersistentActor with ActorLogging {
  override def persistenceId: String = "computer-persistence-id"

  var state = Events()

  def updateState(event: Computed): Unit = {
    state = state.add(event)
  }

  override def receiveCommand: Receive = {
    case command: Compute => persistAsync(Computed(command.execute))(updateState)
    case Snapshot => saveSnapshot(state)
    case SaveSnapshotSuccess(metadata) => log.info(s"Computer snapshot successful: $metadata")
    case SaveSnapshotFailure(metadata, reason) => throw reason
    case Result => sender ! state.list
    case Shutdown => context.stop(self)
  }

  override def receiveRecover: Receive = {
    case computed: Computed => updateState(computed)
    case SnapshotOffer(_, snapshot: Events) => state = snapshot
    case RecoveryCompleted => log.info("Computer snapshot recovery completed.")
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

  test("persistence") {
    val command = Compute(fibonacci, 1)
    assert(command.execute == 1)

    val event = Computed(command.execute)
    assert(event.value == 1)

    for (n <- 1 to 10) computer ! Compute(fibonacci, n)
    Await.result(Future { Thread.sleep(3000) }, 4 seconds)

    computer ! Snapshot

    assert(Await.result(computer ? Result, 3 seconds).asInstanceOf[List[Int]].size == 10)

    computer ! Shutdown
  }
}