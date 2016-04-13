package words

import akka.actor.{Actor, ActorLogging, Stash}

class Listener extends Actor with Stash with ActorLogging {
  val master = context.actorSelection("/user/master")

  override def receive: Receive = {
    case words: Array[String] => master ! CountWords(words)
    case wordsCounted: WordsCounted => log.info(s"Words counted: $wordsCounted")
    case WorkerUnavailable(countWords) => log.error(s"Worker unavailable: $countWords")
  }
}