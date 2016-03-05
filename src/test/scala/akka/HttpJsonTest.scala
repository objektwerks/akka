package akka

import java.time.LocalTime

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import org.scalatest.FunSuite
import spray.json.DefaultJsonProtocol

import scala.io.Source

case class Now(time: String = LocalTime.now.toString)

trait NowProtocols extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val messageFormat = jsonFormat1(Now)
}

trait NowService extends NowProtocols {
  implicit val system: ActorSystem = ActorSystem.create("now", Conf.config)
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()
  val route = path("now") {
    import akka.http.scaladsl.marshalling._
    get {
      complete {
        println("get...")
        ToResponseMarshallable[Now](Now())
      }
    }
  }
  val server = Http().bindAndHandle(route, "localhost", 0)
  server onFailure {
    case e: Exception => println(e); terminate()
  }

  def terminate(): Unit = {
    server map { binding =>
      binding.unbind.onComplete {
        case _ => system.terminate
      }
    }
  }
}

class HttpJsonTest extends FunSuite with NowService {
  test("now") {
    server map { binding =>
      println(s"now: ${binding.localAddress.toString}/now")
      val time = Source.fromURL(s"${binding.localAddress.toString}/now").mkString
      println(time)
      // LocalTime.parse(time)
      terminate()
    }
  }
}