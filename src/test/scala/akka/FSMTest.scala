package akka

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.Await
import scala.concurrent.duration._

sealed trait State
case object Off extends State
case object On extends State

case class Data(flowRate: Int)

case class Event(state: State, data: Data)

class Pump extends Actor with FSM[State, Data] {
  startWith(Off, Data(0))
  when(Off) {
    case Event(On, Data(flowRate)) => goto(On) using Data(flowRate)
  }
  when(On) {
    case Event(Off, Data(flowRate)) => goto(Off) using Data(flowRate)
  }
  initialize()
}

class FSMTest extends FunSuite with BeforeAndAfterAll {
  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  val system: ActorSystem = ActorSystem.create("fsm", Conf.config)
  val pump: ActorRef = system.actorOf(Props[Pump], name = "pump")

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 1 second)
  }

  test("fsm") {
    pump ! Event(On, Data(33))
    pump ! Event(Off, Data(0))
  }
}