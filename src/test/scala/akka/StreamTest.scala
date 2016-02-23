package akka

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import org.scalatest.FunSuite

class StreamTest extends FunSuite {
  implicit val system: ActorSystem = ActorSystem.create("stream", Conf.config)
  implicit val materializer = ActorMaterializer()

  test("source > int") {
    val source: Source[Int, NotUsed] = Source(1 to 10)
    source.runForeach(i => assert(i > 0 && i < 11))(materializer)
  }
}