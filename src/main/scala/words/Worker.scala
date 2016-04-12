package words

import akka.actor.{Actor, ActorLogging}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.cluster.{Cluster, Member, MemberStatus}

class Worker extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case countWords: CountWords => sender ! WordsCounted(countWords.id, toWordCount(countWords.words))
    case state: CurrentClusterState => state.members.filter(_.status == MemberStatus.Up) foreach register
    case MemberUp(member) => register(member)
  }

  private def register(member: Member): Unit = {
    if (member.hasRole("master")) {
      val address = member.address.toString
      context.actorSelection(address) ! RegisterWorker
    }
  }

  private def toWordCount(words: Array[String]): Map[String, Int] = {
    words.groupBy((word: String) => word.toLowerCase).mapValues(_.length)
  }
}