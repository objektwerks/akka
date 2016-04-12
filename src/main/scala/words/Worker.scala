package words

import akka.actor.{Actor, ActorLogging, ActorRef, Props, RootActorPath}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.cluster.{Cluster, Member, MemberStatus}
import akka.contrib.pattern.ReliableProxy

import scala.concurrent.duration._

class Worker extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case countWords: CountWords => sendEvent(sender, WordsCounted(countWords.id, toWordCount(countWords.words)))
    case state: CurrentClusterState => state.members.filter(_.status == MemberStatus.Up) foreach register
    case MemberUp(member) => register(member)
  }

  private def register(member: Member): Unit = {
    if (member.hasRole("master")) {
      context.actorSelection(RootActorPath(member.address) / "user" / "master") ! RegisterWorker
    }
  }

  private def toWordCount(words: Array[String]): Map[String, Int] = {
    words.groupBy((word: String) => word.toLowerCase).mapValues(_.length)
  }

  private def sendEvent(master: ActorRef, wordsCounted: WordsCounted): Unit = {
    val reliableProxy = new ReliableProxy(
      targetPath = master.path,
      retryAfter = 500 millis,
      reconnectAfter = None,
      maxConnectAttempts = None)
    val masterProxy = context.system.actorOf(Props(reliableProxy), "master-proxy")
    masterProxy ! wordsCounted
  }
}