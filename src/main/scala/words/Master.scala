package words

import akka.actor.ActorLogging
import akka.persistence.{PersistentActor, RecoveryCompleted}

import scala.collection.mutable

class Master extends PersistentActor with ActorLogging {
  override def persistenceId = "master-persistence-id"
  val commands = mutable.ListMap.empty[Id, CountWords]
  val addCommand: CountWords => Unit = (countWords: CountWords) => commands += countWords.id -> countWords
  val removeCommand: Id => Unit = (id: Id) => commands -= id

  override def receiveCommand: Receive = {
    case countWords: CountWords => persistAsync(countWords)(addCommand)
    case ReadyForWork if commands.nonEmpty => sender ! commands.head
    case wordsCounted: WordsCounted =>
      removeCommand(wordsCounted.commandId)
      context.system.log.info(wordsCounted.toString)
  }

  override def receiveRecover: Receive = {
    case countWords: CountWords => addCommand(countWords)
    case RecoveryCompleted => log.info("CountWords snapshot recovery completed.")
  }
}