package words

import akka.actor.{ActorLogging, ActorRef, Terminated}
import akka.cluster.Cluster
import akka.persistence._
import akka.persistence.serialization.Snapshot
import akka.util.Timeout

import scala.collection.mutable
import scala.concurrent.duration._
import scala.util.Random

class Master extends PersistentActor with ActorLogging {
  implicit val ec = context.system.dispatcher
  implicit val timeout = Timeout(3 seconds)
  val publisher = context.system.eventStream
  val random = new Random
  var workers = mutable.ArraySeq.empty[ActorRef]
  var commands = Commands()

  Cluster(context.system).registerOnMemberUp {
    context.system.scheduler.schedule(3 seconds, 1 second) {
      sendCommand()
    }
  }

  override def persistenceId = "master-persistence-id"

  override def receiveCommand: Receive = {
    case countWords: CountWords => persist(countWords)(commands.add)
    case wordsCounted: WordsCounted => publisher.publish(wordsCounted)
    case RegisterWorker if !workers.contains(sender) =>
      context watch sender
      workers = workers :+ sender
    case Terminated(worker) => workers = workers.filterNot(_ == worker)
    case Snapshot => saveSnapshot(commands)
    case SaveSnapshotSuccess(metadata) => log.info(s"Save snapshot success: $metadata")
    case SaveSnapshotFailure(metadata, reason) => log.error(s"Save snapshot failure: $metadata; $reason")
  }

  override def receiveRecover: Receive = {
    case countWords: CountWords => commands.add(countWords)
    case SnapshotOffer(metadata, snapshot: Commands) => commands = snapshot
  }

  private def sendCommand(): Unit = {
    if (workers.nonEmpty && commands.nonEmpty) {
      workers(random.nextInt(workers.length)) ! commands.head
      saveSnapshot(commands)
    }
  }
}