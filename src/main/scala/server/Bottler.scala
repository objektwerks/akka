package server

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.util.Timeout
import command.Brew
import event.Brewed

class Bottler extends Actor with ActorLogging {
  implicit val timeout = new Timeout(10, TimeUnit.SECONDS)
  val cluster = Cluster(context.system)
  log.info("Bottler activated!")

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case brew: Brew => context.system.eventStream.publish(Brewed(brew.number, brew.initiated, LocalDateTime.now, brew.recipe))
    case MemberUp(member) => log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) => log.warning("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) => log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case _: MemberEvent => log.warning("Unknown Member event!")
  }
}