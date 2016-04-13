package words

import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}

import scala.collection.mutable
import scala.util.Random

class Master extends Actor with ActorLogging {
  val listener = context.parent
  val workers = mutable.ArrayBuffer.empty[ActorRef]
  val random = new Random

  override def receive: Receive = {
    case countWords: CountWords if workers.isEmpty => sender ! WorkerUnavailable(countWords)
    case countWords: CountWords => workers(random.nextInt(workers.length)) ! countWords
    case wordsCounted: WordsCounted => listener ! wordsCounted
    case RegisterWorker if !workers.contains(sender) =>
      context watch sender
      workers += sender
    case Terminated(worker) => workers -= worker
  }
}