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

class HttpTest extends FunSuite with BeforeAndAfterAll {
  implicit val system: ActorSystem = ActorSystem.create("http", Conf.config)
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()
  val route = path("now") { get { complete(LocalDateTime.now.toString) } }
  val server = Http().bindAndHandle(route, "localhost", 7979)

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 1 second)
  }

  test("now") {
    val dateTimeAsString = Source.fromURL("http://localhost:7979/now").mkString
    LocalDateTime.parse(dateTimeAsString)
  }
}