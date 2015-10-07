package server

import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.ClusterEvent._
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe, SubscribeAck}
import akka.cluster.{Cluster, MemberStatus}
import command.Brew
import domain.Recipe
import event.Brewed

class Brewer(masher: ActorRef) extends Actor with ActorLogging {
  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(topic = "recipe", self)
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
    case brewed: Brewed => mediator ! Publish(topic = "brewed", brewed)

    case SubscribeAck(Subscribe("recipe", None, `self`)) => log.info("Brewer subscribed to recipe topic.")

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