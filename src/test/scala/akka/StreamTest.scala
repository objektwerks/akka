package akka

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import org.scalatest.FunSuite

import scala.concurrent.ExecutionContext

class StreamTest extends FunSuite {
  implicit val system: ActorSystem = ActorSystem.create("stream", Conf.config)
  implicit val materializer = ActorMaterializer()
  implicit val ec = ExecutionContext.global

  test("runForeach") {
    val source: Source[Int, NotUsed] = Source(1 to 10)
    source.runForeach(i => assert(i > 0 && i < 11))
  }

  test("runFold") {
    val source: Source[Int, NotUsed] = Source(1 to 10)
    val result = source.runFold(0)(_ + _)
    result map { r => assert(r == 10) }
  }
}