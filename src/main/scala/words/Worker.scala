package words

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.MemberUp
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.Random

class Worker extends Actor with ActorLogging {
  val cluster = Cluster(context.system)
  val masters = IndexedSeq.empty[ActorRef]
  val random = new Random

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])

  override def postStop(): Unit = cluster.unsubscribe(self)

  implicit val ec = context.system.dispatcher
  context.system.scheduler.schedule(4 seconds, 4 seconds) {
    implicit val timeout = Timeout(3 seconds)
    if (masters.nonEmpty) masters(random.nextInt(masters.length)) ! ReadyForWork
  }

  def receive = {
    case countWords: CountWords => sender ! WordsCounted(toWordCount(countWords.words))
    case MemberUp(member) if member.hasRole("master") => masters :+ member
  }

  def toWordCount(words: Array[String]): Map[String, Int] = {
    words.groupBy((word: String) => word.toLowerCase).mapValues(_.length)
  }
}