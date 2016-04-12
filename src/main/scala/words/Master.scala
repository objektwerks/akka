package words

import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}
import akka.cluster.Cluster
import akka.util.Timeout

import scala.collection.mutable
import scala.concurrent.duration._
import scala.util.Random

class Master extends Actor with ActorLogging {
  val publisher = context.system.eventStream
  val random = new Random
  var workers = mutable.ArraySeq.empty[ActorRef]
  var commands = Commands()

  Cluster(context.system).registerOnMemberUp {
    implicit val ec = context.system.dispatcher
    implicit val timeout = Timeout(3 seconds)
    context.system.scheduler.schedule(3 seconds, 1 second) {
      sendCommand()
    }
  }

  override def receive: Receive = {
    case countWords: CountWords => commands.add(countWords)
    case wordsCounted: WordsCounted => publisher.publish(wordsCounted)
    case RegisterWorker if !workers.contains(sender) =>
      context watch sender
      workers = workers :+ sender
    case Terminated(worker) => workers = workers.filterNot(_ == worker)
  }


  private def sendCommand(): Unit = {
    if (workers.nonEmpty && commands.nonEmpty) {
      workers(random.nextInt(workers.length)) ! commands.head
    }
  }
}