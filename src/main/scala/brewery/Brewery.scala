package brewery

import akka.actor._
import akka.util.Timeout
import brewery.actor._
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._
import scalafx.beans.property.ObjectProperty
import scala.language.postfixOps

object Brewery {
  implicit val timeout = Timeout(10 seconds)
  var commandPropertyListener: Option[ObjectProperty[Command]] = None
  var statePropertyListener: Option[ObjectProperty[State]] = None
  var eventPropertyListener: Option[ObjectProperty[Event]] = None

  val system = ActorSystem.create("brewery", ConfigFactory.load("brewery.conf"))
  val bottler = system.actorOf(Props[Bottler], name = "bottler")
  val kegger = system.actorOf(Props[Kegger], name = "kegger")
  val casker = system.actorOf(Props[Casker], name = "casker")
  val conditioner = system.actorOf(Props(classOf[Conditioner], bottler, kegger, casker), name = "conditioner")
  val fermenter = system.actorOf(Props(classOf[Fermenter], conditioner), name = "fermenter")
  val cooler = system.actorOf(Props(classOf[Cooler], fermenter), name = "cooler")
  val boiler = system.actorOf(Props(classOf[Boiler], cooler), name = "boiler")
  val masher = system.actorOf(Props(classOf[Masher], boiler), name = "masher")
  val brewer = system.actorOf(Props(classOf[Brewer], masher), name = "brewer")

  system.eventStream.subscribe(brewer, classOf[Command])
  system.eventStream.subscribe(brewer, classOf[State])
  system.eventStream.subscribe(brewer, classOf[Event])
  system.log.info("Brewery initialized!")

  def register(commandProperty: ObjectProperty[Command],
               stateProperty: ObjectProperty[State],
               eventProperty: ObjectProperty[Event]): Unit = {
    commandPropertyListener = Some(commandProperty)
    statePropertyListener = Some(stateProperty)
    eventPropertyListener = Some(eventProperty)
  }

  def brew(recipe: Recipe): Unit = brewer ! recipe

  def onCommand(command: Command): Unit = commandPropertyListener foreach { _.value = command }

  def onState(state: State): Unit = statePropertyListener foreach { _.value = state }

  def onEvent(event: Event): Unit = eventPropertyListener foreach { _.value = event }

  def close(): Unit = {
    system.log.info("Brewery closing...")
    Await.result(system.terminate, 3 seconds)
    ()
  }
}