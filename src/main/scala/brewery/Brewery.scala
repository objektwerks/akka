package brewery

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import domain.Recipe
import event.Brewed
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Await, Future}
import scalafx.beans.property.ObjectProperty

object Brewery {
  implicit val ec = ExecutionContext.Implicits.global
  implicit val timeout = new Timeout(10, TimeUnit.SECONDS)
  val log = LoggerFactory.getLogger(this.getClass)
  var brewedPropertyListener: Option[ObjectProperty[Brewed]] = None

  val system = ActorSystem.create("Brewery", ConfigFactory.load("brewery.conf"))
  val bottler: ActorRef = system.actorOf(Props[Bottler], name = "bottler")
  val conditioner: ActorRef = system.actorOf(Props(new Conditioner(bottler)), name = "conditioner")
  val fermenter: ActorRef = system.actorOf(Props(new Fermenter(conditioner)), name = "fermenter")
  val cooler: ActorRef = system.actorOf(Props(new Cooler(fermenter)), name = "cooler")
  val boiler: ActorRef = system.actorOf(Props(new Boiler(cooler)), name = "boiler")
  val masher: ActorRef = system.actorOf(Props(new Masher(boiler)), name = "masher")
  val brewer: ActorRef = system.actorOf(Props(new Brewer(masher)), name = "brewer")
  system.eventStream.subscribe(brewer, classOf[Brewed])

  log.info("Brewery initialized!")

  def register(brewedProperty: ObjectProperty[Brewed]): Unit = {
    brewedPropertyListener = Some(brewedProperty)
  }

  def brew(recipe: Recipe): Unit = {
    Future { brewer ! recipe }
  }

  def brewed(brewed: Brewed): Unit = {
    brewedPropertyListener foreach { _.value = brewed }
  }

  def shutdown(): Unit = {
    Await.result(system.terminate(), 3 seconds)
    log.info("Brewery shutdown!")
  }
}