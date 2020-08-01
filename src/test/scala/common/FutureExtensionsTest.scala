package common

import java.util.UUID

import akka.actor.ActorSystem

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

import common.FutureExtensions._

class FutureExtensionsTest extends AnyFunSuite with Matchers {
  val testConfig = ConfigFactory.load("test.conf")
  val futureExtensionsDispatcherPath = Option("akka.actor.future-extensions-dispatcher")

  test("future with timeout > success") {
    withTimeout(config = testConfig,
                dispatcherPath = futureExtensionsDispatcherPath,
                akkaTimeout = 3 seconds,
                futureSleep = 2 seconds).onComplete {
      case Success(result) => result shouldBe true
      case Failure(_) => fail
    }
  }

  test("future with timeout > failure") {
    withTimeout(config = testConfig,
                dispatcherPath = futureExtensionsDispatcherPath,
                akkaTimeout = 2 seconds,
                futureSleep = 3 seconds).onComplete {
      case Success(_) => fail
      case Failure(error) => error.getMessage.nonEmpty shouldBe true
    }
  }

  test("future factorial > success") {
    implicit val system = ActorSystem(UUID.randomUUID.toString, testConfig)
    implicit val dispatcher = futureExtensionsDispatcherPath match {
      case Some(path) => system.dispatchers.lookup(path)
      case None => system.dispatcher
    }
    implicit val timeout = 3 seconds

    Future {
      factorial(9)
    }.withTimeout(new TimeoutException("Future timed out!")).onComplete {
      case Success(result) => result shouldBe 362880
      case Failure(_) => fail
    }
  }

  def withTimeout(config: Config,
                  dispatcherPath: Option[String],
                  akkaTimeout: FiniteDuration,
                  futureSleep: FiniteDuration): Future[Boolean] = {
    implicit val system = ActorSystem(UUID.randomUUID.toString, config)
    implicit val dispatcher = dispatcherPath match {
      case Some(path) => system.dispatchers.lookup(path)
      case None => system.dispatcher
    }
    implicit val timeout = akkaTimeout

    lazy val future = Future {
      Thread.sleep(futureSleep.toMillis)
      true
    }
    future.withTimeout(new TimeoutException("Future timed out!"))
  }

  @tailrec
  final def factorial(n: Long, acc: Long = 1): Long = n match {
    case i if i < 1 => acc
    case _ => factorial(n - 1, acc * n)
  }
}