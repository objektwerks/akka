package words

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.MemberUp

import scala.collection.mutable
import scala.util.Random

class Worker extends Actor with ActorLogging {
  val cluster = Cluster(context.system)
  val masters = mutable.ArraySeq.empty[ActorRef]
  val random = new Random

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case countWords: CountWords =>
      sender ! WordsCounted(countWords.id, toWordCount(countWords.words))
      readyForCommand()
    case MemberUp(member) if member.hasRole("master") =>
      masters :+ member
      readyForCommand()
  }

  private def readyForCommand(): Unit = masters(random.nextInt(masters.length)) ! ReadyForCommand

  private def toWordCount(words: Array[String]): Map[String, Int] = {
    words.groupBy((word: String) => word.toLowerCase).mapValues(_.length)
  }
}