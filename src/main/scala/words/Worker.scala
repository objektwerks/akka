package words

import akka.actor.{Actor, ActorLogging}

class Worker extends Actor with ActorLogging {
  override def receive: Receive = {
    case countWords: CountWords => sender ! WordsCounted(countWords, count(countWords.words))
  }

  private def count(words: List[String]): Map[String, Int] = {
    words.groupBy((word: String) => word.toLowerCase).mapValues(_.length)
  }
}