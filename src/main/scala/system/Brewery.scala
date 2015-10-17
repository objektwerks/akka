package system

import java.util.concurrent.TimeUnit

import actor._
import akka.actor._
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import command.Command
import domain.Recipe
import event.Event
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scalafx.beans.property.ObjectProperty

object Brewery {
  implicit val ec = ExecutionContext.Implicits.global
  implicit val timeout = new Timeout(10, TimeUnit.SECONDS)
  val log = LoggerFactory.getLogger(this.getClass)
  var commandPropertyListener: Option[ObjectProperty[Command]] = None
  var eventPropertyListener: Option[ObjectProperty[Event]] = None
  val system = ActorSystem.create("Brewery", ConfigFactory.load("brewery.conf"))
  val brewer: ActorRef = system.actorOf(Props[Brewer], name = "brewer")
  system.eventStream.subscribe(brewer, classOf[Command])
  system.eventStream.subscribe(brewer, classOf[Event])
  system.actorOf(Props[Listener], name = "listener")
  log.info("Brewery initialized!")

  def register(commandProperty: ObjectProperty[Command], eventProperty: ObjectProperty[Event]): Unit = {
    commandPropertyListener = Some(commandProperty)
    eventPropertyListener = Some(eventProperty)
  }

  def brew(recipe: Recipe): Unit = {
    Future { brewer ! recipe }
  }

  def command(command: Command): Unit = {
    commandPropertyListener foreach { _.value = command }
  }

  def event(event: Event): Unit = {
    eventPropertyListener foreach { _.value = event }
  }

  def shutdown(): Unit = {
    Await.result(system.terminate(), 3 seconds)
    log.info("Brewery shutdown!")
  }
}