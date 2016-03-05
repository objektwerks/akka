package akka

import java.time.LocalTime

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._
import spray.json.DefaultJsonProtocol

case class Now(time: String = LocalTime.now.toString)

trait NowProtocols extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val messageFormat = jsonFormat1(Now)
}

trait NowService extends NowProtocols {
  import akka.http.scaladsl.server.Directives._
  val route = path("now") {
    get {
      complete(ToResponseMarshallable[Now](Now()))
    }
  }
}

class HttpJsonTest extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterAll with NowService {
  val actorRefFactory = ActorSystem.create("now", Conf.config)
  val server = Http().bindAndHandle(route, "localhost", 0)

  override protected def beforeAll(): Unit = {
    server map { binding =>
      val address = binding.localAddress.toString + "/now"
      println(s"now service address: $address")
    }
  }

  override protected def afterAll(): Unit = {
    server map { binding =>
      binding.unbind.onComplete {
        case _ => println("now service terminating..."); system.terminate
      }
    }
  }

  "NowService" should {
    "respond with current time" in {
      Get("/now") ~> route ~> check {
        println(s"now service time: ${responseAs[Now].time}")
        responseAs[Now].time.nonEmpty shouldBe true
      }
    }
  }
}