package words

import akka.actor.{Actor, ActorLogging, ActorRef, Stash, Terminated}

import scala.collection.mutable
import scala.util.Random

class Master extends Actor with Stash with ActorLogging {
  val publisher = context.system.eventStream
  val random = new Random
  val workers = mutable.ArrayBuffer.empty[ActorRef]

  override def receive: Receive = {
    case countWords: CountWords if workers.isEmpty => sender ! WorkerUnavailable(countWords)
    case countWords: CountWords => workers(random.nextInt(workers.length)) ! countWords
    case wordsCounted: WordsCounted => publisher.publish(wordsCounted)
    case RegisterWorker if !workers.contains(sender) =>
      context watch sender
      workers += sender
    case Terminated(worker) => workers -= worker
  }
}