package app

import java.util.concurrent.TimeUnit

import akka.actor.{ActorLogging, Actor, ActorRef}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.util.Timeout

class Masher(boiler: ActorRef) extends Actor with ActorLogging {
  implicit val timeout = new Timeout(10, TimeUnit.SECONDS)
  val cluster = Cluster(context.system)
  log.info("Masher activated!")

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case batch: Brew => boiler ! batch
    case MemberUp(member) => log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) => log.warning("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) => log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case _: MemberEvent => log.warning("Unknown Member event!")
  }
}