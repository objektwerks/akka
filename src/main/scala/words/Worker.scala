package words

import akka.actor.{Actor, ActorLogging, RootActorPath}
import akka.cluster.{Cluster, Member, MemberStatus}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}

class Worker extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case countWords: CountWords => sender ! WordsCounted(toWordCount(countWords.words))
    case state: CurrentClusterState => state.members.filter(_.status == MemberStatus.Up) foreach register
  }

  def toWordCount(words: Array[String]): Map[String, Int] = {
    words.groupBy((word: String) => word.toLowerCase).mapValues(_.length)
  }

  def register(member: Member): Unit = {
    if (member.hasRole("master"))
      context.actorSelection(RootActorPath(member.address) / "user" / "master") ! WorkerRegistration
  }
}