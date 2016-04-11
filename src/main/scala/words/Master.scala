package words

import akka.actor.ActorLogging
import akka.persistence._
import akka.persistence.serialization.Snapshot

class Master extends PersistentActor with ActorLogging {
  val publisher = context.system.eventStream
  var commands = Commands()

  override def persistenceId = "master-persistence-id"

  override def receiveCommand: Receive = {
    case countWords: CountWords => persistAsync(countWords)(commands.add)
    case ReadyForCommand if commands.nonEmpty => sender ! commands.head
    case wordsCounted: WordsCounted =>
      commands.remove(wordsCounted.commandId)
      self ! Snapshot
      publisher.publish(wordsCounted)
    case Snapshot => saveSnapshot(commands)
    case SaveSnapshotSuccess(metadata) => log.info(s"Save snapshot success: $metadata")
    case SaveSnapshotFailure(metadata, reason) => log.error(s"Save snapshot failure: $metadata; $reason")
  }

  override def receiveRecover: Receive = {
    case countWords: CountWords => commands.add(countWords)
    case SnapshotOffer(metadata, snapshot: Commands) =>
      commands = snapshot
      log.info(s"Snapshot Offer accepted: $metadata")
    case RecoveryCompleted => log.info("Recovery completed.")
  }
}