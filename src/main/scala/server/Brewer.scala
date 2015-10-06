package server

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, SubscribeAck}
import akka.cluster.pubsub.{DistributedPubSub, DistributedPubSubMediator}
import akka.util.Timeout
import command.Brew
import domain.Recipe
import event.Brewed

class Brewer(masher: ActorRef) extends Actor with ActorLogging {
  import DistributedPubSubMediator.Subscribe
  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(topic = "recipe", self)

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
    case brewed: Brewed => mediator ! Publish(topic = "brewed", brewed)
    case SubscribeAck(Subscribe("recipe", None, `self`)) => log.info("Brewer subscribed to recipe topic.")
    case MemberUp(member) => log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) => log.warning("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) => log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case _: MemberEvent => log.warning("Unknown Member event!")
  }
}