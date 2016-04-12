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
      if (workers.nonEmpty) workers(random.nextInt(workers.length)) ! commands.head
    case wordsCounted: WordsCounted =>
      commands.remove(wordsCounted.commandId)
      self ! Snapshot
      publisher.publish(wordsCounted)
    case Snapshot => saveSnapshot(commands)
    case SaveSnapshotSuccess(metadata) => log.info(s"Save snapshot success: $metadata")
    case SaveSnapshotFailure(metadata, reason) => log.error(s"Save snapshot failure: $metadata; $reason")
    case RegisterWorker if !workers.contains(sender) =>
      context watch sender
      workers = workers :+ sender
    case Terminated(worker) => workers = workers.filterNot(_ == worker)
  }

  override def receiveRecover: Receive = {
    case countWords: CountWords => commands.add(countWords)
    case SnapshotOffer(metadata, snapshot: Commands) =>
      commands = snapshot
      log.info(s"Snapshot offer accepted: $metadata")
    case RecoveryCompleted => log.info("Recovery completed.")
  }
}