package akka

import java.time.LocalDateTime

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.io.Source

class HttpTest extends FunSuite with BeforeAndAfterAll {
  implicit val system: ActorSystem = ActorSystem.create("http", Conf.config)
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()
  val route = path("now") { get { complete(LocalDateTime.now.toString) } }
  val server = Http().bindAndHandle(route, "localhost", 7979)

  override protected def beforeAll(): Unit = {
    server onFailure {
      case e: Exception => println(e.getMessage)
    }
  }

  override protected def afterAll(): Unit = {
    server.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }

  test("now") {
    val dateTimeAsString = Source.fromURL("http://localhost:7979/now").mkString
    LocalDateTime.parse(dateTimeAsString)
  }
}