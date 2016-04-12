package words

import akka.actor.{Actor, ActorLogging}
import akka.cluster.{Cluster, Member, MemberStatus}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberRemoved, MemberUp}

import scala.collection.mutable
import scala.util.Random

class Worker extends Actor with ActorLogging {
  val cluster = Cluster(context.system)
  val masters = mutable.ArrayBuffer.empty[Member]
  val random = new Random

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case countWords: CountWords =>
      sender ! WordsCounted(countWords.id, toWordCount(countWords.words))
      readyToCountWords()
    case state: CurrentClusterState =>
      state.members.filter(_.status == MemberStatus.Up).filter(_.hasRole("master")) foreach registerMember
    case MemberUp(member) if member.hasRole("master") =>
      registerMember(member)
      readyToCountWords()
    case MemberRemoved(member, previousStatus) if member.hasRole("master") =>
      unregisterMember(member)
      readyToCountWords()
  }

  private def registerMember(member: Member): Unit = {
    if (!masters.contains(member)) masters += member
  }

  private def unregisterMember(member: Member): Unit = {
    if (!masters.contains(member)) masters -= member
  }

  private def readyToCountWords(): Unit = {
    if (masters.nonEmpty) {
      val address = masters(random.nextInt(masters.length)).address.toString
      val count = masters.size
      context.actorSelection(address) ! ReadyToCountWords(count)
    }
  }

  private def toWordCount(words: Array[String]): Map[String, Int] = {
    words.groupBy((word: String) => word.toLowerCase).mapValues(_.length)
  }
}