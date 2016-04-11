package words

import akka.actor.{Actor, ActorLogging}

import scala.collection.mutable

class Master extends Actor with ActorLogging {
  val state = mutable.Queue.empty[CountWords]

  def receive = {
    case countWords: CountWords => state enqueue countWords
    case readyForWork: ReadyForWork if state.nonEmpty => sender ! state.dequeue
    case wordsCounted: WordsCounted => context.system.log.info(wordsCounted.toString)
  }
}