package cluster

import akka.actor.{Actor, ActorLogging}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, MemberStatus}


class Listener extends Actor with ActorLogging {
  override def preStart(): Unit = {
    Cluster(context.system).subscribe(self, classOf[ClusterDomainEvent])
  }

  override def postStop(): Unit = {
    Cluster(context.system).unsubscribe(self)
  }

  override def receive: Receive = {
    case MemberUp(member) => log.info(s"*** Member Up Event: $member")
    case MemberExited(member) => log.info(s"*** Member Exited: $member")
    case MemberRemoved(member, previousState) =>
      if(previousState == MemberStatus.Exiting) log.info(s"*** Member Removed: $member")
      else log.info(s"*** Member Removed | Unreachable: $member")
    case UnreachableMember(member) => log.info(s"*** Member Unreachable: $member")
    case ReachableMember(member) => log.info(s"*** Member Reachable: $member")
    case state: CurrentClusterState => log.info(s"*** Current Cluster State: ${state.toString}")
  }
}