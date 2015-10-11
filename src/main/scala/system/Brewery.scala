package system

import java.util.concurrent.TimeUnit

import actor._
import akka.actor._
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, MemberStatus}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import domain.Recipe
import event.{Brewed, Stage}
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scalafx.beans.property.ObjectProperty

object Brewery {
  implicit val ec = ExecutionContext.Implicits.global
  implicit val timeout = new Timeout(10, TimeUnit.SECONDS)
  val log = LoggerFactory.getLogger(this.getClass)
  var stagePropertyListener: Option[ObjectProperty[Stage]] = None
  var brewedPropertyListener: Option[ObjectProperty[Brewed]] = None

  val system = ActorSystem.create("Brewery", ConfigFactory.load("brewery.conf"))
  val bottler: ActorRef = system.actorOf(Props[Bottler], name = "bottler")
  val conditioner: ActorRef = system.actorOf(Props(new Conditioner(bottler)), name = "conditioner")
  val fermenter: ActorRef = system.actorOf(Props(new Fermenter(conditioner)), name = "fermenter")
  val cooler: ActorRef = system.actorOf(Props(new Cooler(fermenter)), name = "cooler")
  val boiler: ActorRef = system.actorOf(Props(new Boiler(cooler)), name = "boiler")
  val masher: ActorRef = system.actorOf(Props(new Masher(boiler)), name = "masher")
  val brewer: ActorRef = system.actorOf(Props(new Brewer(masher)), name = "brewer")
  system.eventStream.subscribe(brewer, classOf[Stage])
  system.eventStream.subscribe(brewer, classOf[Brewed])
  system.actorOf(Props[Listener], name = "listener")

  log.info("Brewery initialized!")

  def register(stageProperty: ObjectProperty[Stage], brewedProperty: ObjectProperty[Brewed]): Unit = {
    stagePropertyListener = Some(stageProperty)
    brewedPropertyListener = Some(brewedProperty)
  }

  def brew(recipe: Recipe): Unit = {
    Future { brewer ! recipe }
  }

  def stage(stage: Stage): Unit = {
    stagePropertyListener foreach { _.value = stage }
  }

  def brewed(brewed: Brewed): Unit = {
    brewedPropertyListener foreach { _.value = brewed }
  }

  def shutdown(): Unit = {
    Await.result(system.terminate(), 3 seconds)
    log.info("Brewery shutdown!")
  }
}

class Listener extends Actor with ActorLogging {
  override def preStart(): Unit = {
    Cluster(context.system).subscribe(self, classOf[ClusterDomainEvent])
  }

  override def postStop(): Unit = {
    Cluster(context.system).unsubscribe(self)
    super.postStop()
  }

  override def receive: Receive = {
    case MemberUp(member) => log.info(s"$member UP.")
    case MemberExited(member) => log.info(s"$member EXITED.")
    case MemberRemoved(member, previousState) =>
      if(previousState == MemberStatus.Exiting) log.info(s"$member previously gracefully EXITED, REMOVED.")
      else log.info(s"$member previously downed after UNREACHABLE, REMOVED.")
    case UnreachableMember(member) => log.info(s"$member UNREACHABLE")
    case ReachableMember(member) => log.info(s"$member REACHABLE")
    case state: CurrentClusterState => log.info(s"CURRENT CLUSTER STATE: $state")
  }
}