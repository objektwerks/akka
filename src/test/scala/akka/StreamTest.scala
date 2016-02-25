package akka

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

class StreamTest extends FunSuite with BeforeAndAfterAll{
  implicit val system: ActorSystem = ActorSystem.create("stream", Conf.config)
  implicit val materializer = ActorMaterializer()
  implicit val ec = ExecutionContext.global
  val source: Source[Int, NotUsed] = Source(1 to 10)

  override protected def afterAll(): Unit = {
    Await.result(system.terminate(), 1 second)
  }

  test("run foreach") {
    source.runForeach { i => assert(i > 0 && i < 11) }
  }

  test("run fold") {
    source.runFold(0)(_ + _) map { r => assert(r == 10) }
  }

  test("run reduce") {
    source.runReduce(_ + _) map { r => assert(r == 10) }
  }

  test("run with") {
    source.runWith(Sink.fold(0)(_ + _)) map { r => assert(r == 10) }
    source.runWith(Sink.foreach { i => assert(i > 0 && i < 11) })
  }
}