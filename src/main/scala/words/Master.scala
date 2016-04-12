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
  val workers = mutable.ArrayBuffer.empty[ActorRef]
  val commands = Commands()

  Cluster(context.system).registerOnMemberUp {
    implicit val ec = context.system.dispatcher
    implicit val timeout = Timeout(3 seconds)
    context.system.scheduler.schedule(3 seconds, 1 second) {
      if (workers.nonEmpty && commands.nonEmpty) {
        workers(random.nextInt(workers.length)) ! commands.head
      }
    }
  }

  override def receive: Receive = {
    case countWords: CountWords => commands.add(countWords)
    case wordsCounted: WordsCounted => publisher.publish(wordsCounted)
    case RegisterWorker if !workers.contains(sender) =>
      context watch sender
      workers += sender
    case Terminated(worker) => workers -= worker
  }
}