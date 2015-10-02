package brewery

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.util.Timeout
import app.{Brew, Brewed, Recipe}

class Brewer(masher: ActorRef) extends Actor with ActorLogging {
  implicit val timeout = new Timeout(10, TimeUnit.SECONDS)
  val batchNumber = new AtomicInteger()
  val cluster = Cluster(context.system)
  log.info("Brewer activated!")

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case recipe: Recipe => masher ! Brew(batchNumber.incrementAndGet(), LocalDateTime.now, recipe)
    case brewed: Brewed => log.info(s"Brewed: $brewed")
    case MemberUp(member) => log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) => log.warning("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) => log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case _: MemberEvent => log.warning("Unknown Member event!")
  }
}