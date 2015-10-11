package actor

import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, MemberStatus}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import command.Brew
import domain.Recipe
import event.{Brewed, Stage}
import system.Brewery

class Brewer(masher: ActorRef) extends Actor with ActorLogging {
  val batchNumber = new AtomicInteger()
  var router = {
    val routees = Vector.fill(3) {
      val assistant = context.actorOf(Props[Assistant])
      context watch assistant
      ActorRefRoutee(assistant)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  override def preStart(): Unit = {
    Cluster(context.system).subscribe(self, classOf[ClusterDomainEvent])
  }

  override def postStop(): Unit = {
    Cluster(context.system).unsubscribe(self)
    super.postStop()
  }

  override def receive: Receive = {
    case recipe: Recipe => masher ! Brew(batchNumber.incrementAndGet(), LocalDateTime.now, recipe) // router.route()
    case stage: Stage => Brewery.stage(stage)
    case brewed: Brewed => Brewery.brewed(brewed)

    case MemberUp(member) => log.info(s"$member UP.")
    case MemberExited(member) => log.info(s"$member EXITED.")
    case MemberRemoved(member, previousState) =>
      if(previousState == MemberStatus.Exiting) {
        log.info(s"$member previously gracefully EXITED, REMOVED.")
      } else {
        log.info(s"$member previously downed after UNREACHABLE, REMOVED.")
      }
    case UnreachableMember(member) => log.info(s"$member UNREACHABLE")
    case ReachableMember(member) => log.info(s"$member REACHABLE")
    case state: CurrentClusterState => log.info(s"CURRENT CLUSTER STATE: $state")
  }
}