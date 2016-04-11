package words

import akka.actor.ActorLogging
import akka.persistence.serialization.Snapshot
import akka.persistence._

class Master extends PersistentActor with ActorLogging {
  override def persistenceId = "master-persistence-id"
  var commands = Commands()

  override def receiveCommand: Receive = {
    case countWords: CountWords => persistAsync(countWords)(commands.add)
    case ReadyForCommand if commands.nonEmpty => sender ! commands.head
    case wordsCounted: WordsCounted =>
      commands.remove(wordsCounted.commandId)
      self ! Snapshot
      log.info(wordsCounted.toString)
    case Snapshot => saveSnapshot(commands)
    case SaveSnapshotSuccess(metadata) => log.info(s"Command snapshot success: $metadata")
    case SaveSnapshotFailure(metadata, reason) => log.error(s"Command snapshot failure: $metadata; $reason")
  }

  override def receiveRecover: Receive = {
    case countWords: CountWords => commands.add(countWords)
    case SnapshotOffer(_, snapshot: Commands) => commands = snapshot
    case RecoveryCompleted => log.info("Command snapshot recovery completed.")
  }
}