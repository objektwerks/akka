package akka

import java.time.LocalTime

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._
import spray.json.DefaultJsonProtocol

import scala.util.{Failure, Success}

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
        if (now.time.isEmpty) complete(StatusCodes.UnprocessableEntity) else complete(StatusCodes.OK)
      }
    }
  }
}

class HttpJsonTest extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterAll with NowService {
  val actorRefFactory = ActorSystem.create("now", Conf.config)
  val server = Http().bindAndHandle(routes, "localhost", 0)

  override protected def beforeAll(): Unit = {
    server onComplete {
      case Success(binding) => println(s"now service: ${binding.localAddress.toString}/now")
      case Failure(failure) => println(s"now service bind failed: ${failure.getMessage}")
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
    "handle get and post." in {
      Get("/now") ~> routes ~> check {
        responseAs[Now].time.nonEmpty shouldBe true
      }
      Post("/now", Now()) ~> routes ~> check {
        status shouldBe OK
      }
    }
  }
}