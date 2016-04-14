package words

import akka.actor.{Actor, ActorLogging, RootActorPath}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.cluster.{Cluster, Member, MemberStatus}

class Worker extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case countWords: CountWords => sender ! WordsCounted(countWords, count(countWords.words))
    case state: CurrentClusterState => state.members.filter(_.status == MemberStatus.Up) foreach register
    case MemberUp(member) => register(member)
  }

  private def register(member: Member): Unit = {
    if (member.hasRole("master")) {
      context.actorSelection(RootActorPath(member.address) / "user" / "master") ! RegisterWorker
    }
  }

  private def count(words: List[String]): Map[String, Int] = {
    words.groupBy((word: String) => word.toLowerCase).mapValues(_.length)
  }
}