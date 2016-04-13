package words

import akka.actor.{Actor, ActorLogging}

class Listener extends Actor with ActorLogging {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case countWords: CountWords => publisher.publish(countWords)
    case wordsCounted: WordsCounted => log.info(s"Words counted: $wordsCounted")
    case WorkerUnavailable(countWords) => log.error(s"Worker unavailable: $countWords")
  }
}