package akka

import java.time.LocalDateTime

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.Source

class HttpStringTest extends FunSuite with BeforeAndAfterAll {
  implicit val system: ActorSystem = ActorSystem.create("http", Conf.config)
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()
  val route = path("now") { get { complete(LocalDateTime.now.toString) } }
  val server = Http().bindAndHandle(route, "localhost", 0)

  override protected def beforeAll(): Unit = {
    server onFailure {
      case e: Exception => throw e
    }
  }

  override protected def afterAll(): Unit = {
    server map { binding =>
      binding.unbind.onComplete { _ => Await.result(system.terminate(), 1 second) }
    }
  }

  test("now") {
    server map { binding =>
      val dateTimeAsString = Source.fromURL(s"${binding.localAddress.toString}/now").mkString
      LocalDateTime.parse(dateTimeAsString)
    }
  }
}