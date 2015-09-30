package akka

import java.util.concurrent.TimeUnit

import akka.util.Timeout
import app.{Batch, BatchEventListener, Brewery, IPA}
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

class BreweryTest extends FunSuite with BeforeAndAfterAll {
  implicit val ec = ExecutionContext.global
  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  val listener = new BatchEventListener() {
    override def onEvent(batch: Batch): Unit = {
      assert(batch != null)
      println(s"*** Batch received: $batch")
    }
  }
  val brewery = new Brewery(listener)

  override protected def afterAll(): Unit = {
    Await.result(brewery.system.terminate(), 3 seconds)
  }

  test("brew") {
    brewery.brew(IPA())
    Await.result(Future { Thread.sleep(2000) }, 3 seconds)
  }
}