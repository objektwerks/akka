package app

import akka.actor.{ActorLogging, Actor, ActorRef}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

class Cooler(fermenter: ActorRef) extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case batch: Batch => fermenter ! batch
    case MemberUp(member) => log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) => log.warning("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) => log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case _: MemberEvent => log.warning("Unknown Member event!")
  }
}