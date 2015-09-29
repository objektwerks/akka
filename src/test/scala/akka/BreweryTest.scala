package akka

import java.util.concurrent.TimeUnit

import akka.util.Timeout
import app.{Batch, BatchEventListener, Brewery, IPA}
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

class BreweryTest extends FunSuite with BeforeAndAfterAll {
  val logger = LoggerFactory.getLogger(this.getClass)
  implicit val ec = ExecutionContext.global
  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  val listener = new BatchEventListener() {
    override def onEvent(batch: Batch): Unit = {
      assert(batch != null)
      logger.info("*********")
      logger.info(s"Batch received: $batch")
      logger.info("*********")
    }
  }
  val brewery = new Brewery(listener)

  override protected def afterAll(): Unit = {
    Await.result(brewery.system.terminate(), 3 seconds)
  }

  test("brew") {
    brewery.brew(IPA())
    logger.info("Brewing...")
    Await.result(Future { Thread.sleep(3000) }, 4 seconds)
  }
}