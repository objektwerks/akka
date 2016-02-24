package akka

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import org.scalatest.FunSuite

import scala.concurrent.ExecutionContext

class StreamTest extends FunSuite {
  implicit val system: ActorSystem = ActorSystem.create("stream", Conf.config)
  implicit val materializer = ActorMaterializer()
  implicit val ec = ExecutionContext.global

  test("run foreach") {
    val source: Source[Int, NotUsed] = Source(1 to 10)
    source.runForeach(i => assert(i > 0 && i < 11))
  }

  test("run fold") {
    val source: Source[Int, NotUsed] = Source(1 to 10)
    val result = source.runFold(0)(_ + _)
    result map { r => assert(r == 10) }
  }

  test("run reduce") {
    val source: Source[Int, NotUsed] = Source(1 to 10)
    val result = source.runReduce(_ + _)
    result map { r => assert(r == 10) }
  }

  test("run with") {
    val source: Source[Int, NotUsed] = Source(1 to 10)
    val result = source.runWith(Sink.fold(0)(_ + _))
    result map { r => assert(r == 10) }
  }
}