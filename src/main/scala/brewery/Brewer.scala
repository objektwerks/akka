package brewery

import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, MemberStatus}
import command.Brew
import domain.Recipe
import event.{Brewed, Stage}

class Brewer(masher: ActorRef) extends Actor with ActorLogging {
  val batchNumber = new AtomicInteger()

  override def preStart(): Unit = {
    Cluster(context.system).subscribe(self, classOf[ClusterDomainEvent])
  }

  override def postStop(): Unit = {
    Cluster(context.system).unsubscribe(self)
    super.postStop()
  }

  override def receive: Receive = {
    case recipe: Recipe => masher ! Brew(batchNumber.incrementAndGet(), LocalDateTime.now, recipe)
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