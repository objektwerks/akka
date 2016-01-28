package akka

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.Await
import scala.concurrent.duration._

case object Ready
case object Swim
case object Bike
case object Run
case object Finish

class Triathlete extends Actor with ActorLogging {
  def receive = prepare

  def prepare: Actor.Receive = {
    case Ready => log.info("Triathlete ready!")
    case Swim => log.info("Triathlete swimming!"); context.become(swim)
  }

  def swim: Actor.Receive = {
    case Bike => log.info("Triathlete biking!"); context.become(bike)
  }

  def bike: Actor.Receive = {
    case Run => log.info("Triathlete running!"); context.become(run)
  }

  def run: Actor.Receive = {
    case Finish => log.info("Triathlete finished race!"); context.become(prepare)
  }

  override def unhandled(message: Any): Unit = {
    super.unhandled(message)
    log.info(s"Triathlete failed to handle message: $message.")
  }
}

class BehaviorTest extends FunSuite with BeforeAndAfterAll {
  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  val system: ActorSystem = ActorSystem.create("behavior", Conf.config)
  val triathlete: ActorRef = system.actorOf(Props[Triathlete], name = "triathlete")

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 1 second)
  }

  test("race") {
    race()
  }

  private def race(): Unit = {
    triathlete ! Ready
    triathlete ! Swim
    triathlete ! Bike
    triathlete ! Run
    triathlete ! Finish
  }
}