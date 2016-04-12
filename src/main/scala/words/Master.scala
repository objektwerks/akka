package words

import akka.actor.{ActorLogging, ActorRef, Terminated}
import akka.persistence._
import akka.persistence.serialization.Snapshot

import scala.collection.mutable
import scala.util.Random

class Master extends PersistentActor with ActorLogging {
  val publisher = context.system.eventStream
  val random = new Random
  var workers = mutable.ArraySeq.empty[ActorRef]
  var commands = Commands()

  override def persistenceId = "master-persistence-id"

  override def receiveCommand: Receive = {
    case countWords: CountWords =>
      persistAsync(countWords)(commands.add)
      sendCommand()
    case wordsCounted: WordsCounted =>
      publisher.publish(wordsCounted)
      sendCommand()
    case RegisterWorker if !workers.contains(sender) =>
      context watch sender
      workers = workers :+ sender
      sendCommand()
    case Terminated(worker) => workers = workers.filterNot(_ == worker)
    case Snapshot => saveSnapshot(commands)
    case SaveSnapshotSuccess(metadata) => log.info(s"Save snapshot success: $metadata")
    case SaveSnapshotFailure(metadata, reason) => log.error(s"Save snapshot failure: $metadata; $reason")
  }

  override def receiveRecover: Receive = {
    case countWords: CountWords => commands.add(countWords)
    case SnapshotOffer(metadata, snapshot: Commands) =>
      commands = snapshot
      log.info(s"Snapshot offer accepted: $metadata")
    case RecoveryCompleted => log.info("Recovery completed.")
  }

  private def sendCommand(): Unit = {
    if (workers.nonEmpty && commands.nonEmpty) {
      workers(random.nextInt(workers.length)) ! commands.head
    }
  }
}