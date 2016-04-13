package words

import akka.actor.{Actor, ActorLogging}

class Listener extends Actor with ActorLogging {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case words: Array[String] => publisher.publish(CountWords(words))
    case wordsCounted: WordsCounted => log.info(s"Words counted: $wordsCounted")
    case WorkerUnavailable(countWords) => log.error(s"Worker unavailable: $countWords")
  }
}