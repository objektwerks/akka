package akka

import java.time.LocalTime

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._
import spray.json.DefaultJsonProtocol

case class Now(time: String = LocalTime.now.toString)

trait NowService extends DefaultJsonProtocol with SprayJsonSupport {
  import akka.http.scaladsl.server.Directives._
  implicit val nowFormat = jsonFormat1(Now)

  val routes = path("now") {
    get {
      complete(ToResponseMarshallable[Now](Now()))
    } ~
    post {
      entity(as[Now]) { now =>
        require(now.time.nonEmpty)
        complete(StatusCodes.OK)
      }
    }
  }
}

class HttpJsonTest extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterAll with NowService {
  val actorRefFactory = ActorSystem.create("now", Conf.config)
  val server = Http().bindAndHandle(routes, "localhost", 0)

  override protected def beforeAll(): Unit = {
    server map { binding =>
      val address = binding.localAddress.toString + "/now"
      println(s"now service: $address")
    }
  }

  override protected def afterAll(): Unit = {
    server map { binding =>
      binding.unbind.onComplete {
        case _ => system.terminate
      }
    }
  }

  "NowService" should {
    "respond with current time" in {
      Get("/now") ~> routes ~> check {
        responseAs[Now].time.nonEmpty shouldBe true
      }
      Post("/now", Now()) ~> routes ~> check {
        status shouldBe OK
      }
    }
  }
}