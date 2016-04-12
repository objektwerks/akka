package words

import akka.actor.{ActorLogging, ActorRef, Props, Terminated}
import akka.contrib.pattern.ReliableProxy
import akka.persistence._
import akka.persistence.serialization.Snapshot

import scala.collection.mutable
import scala.concurrent.duration._
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
      commands.remove(wordsCounted.commandId)
      self ! Snapshot
      publisher.publish(wordsCounted)
      sendCommand()
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
    case SnapshotOffer(metadata, snapshot: Commands) =>
      commands = snapshot
      log.info(s"Snapshot offer accepted: $metadata")
    case RecoveryCompleted => log.info("Recovery completed.")
  }

  private def sendCommand(): Unit = {
    if (workers.nonEmpty) {
      val worker = workers(random.nextInt(workers.length))
      val reliableProxy = new ReliableProxy(
        targetPath = worker.path,
        retryAfter = 500 millis,
        reconnectAfter = None,
        maxConnectAttempts = None)
      val workerProxy = context.system.actorOf(Props(reliableProxy), "worker-proxy")
      workerProxy ! commands.headAsCopy
    }
  }
}