package words

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.MemberUp
import akka.util.Timeout

import scala.io.Source
import scala.concurrent.duration._
import scala.util.Random

class Listener extends Actor with ActorLogging {
  val words = Source.fromInputStream(getClass.getResourceAsStream("/license.mit")).mkString.split("\\P{L}+")
  val (left, right) = words.splitAt(words.size / 2)
  val cluster = Cluster(context.system)
  val masters = IndexedSeq.empty[ActorRef]
  val random = new Random

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])

  override def postStop(): Unit = cluster.unsubscribe(self)

  implicit val ec = context.system.dispatcher
  implicit val timeout = Timeout(3 seconds)
  context.system.scheduler.schedule(2 seconds, 2 seconds) {
    if (masters.nonEmpty) {
      masters(random.nextInt(masters.length)) ! CountWords(left)
      masters(random.nextInt(masters.length)) ! CountWords(right)
    }
  }

  def receive = {
    case MemberUp(member) if member.hasRole("master") => masters :+ member
  }
}