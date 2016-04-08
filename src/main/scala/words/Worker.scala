package words

import akka.actor.{Actor, ActorLogging}

class Worker extends Actor with ActorLogging {
  def receive = {
    case countWords: CountWords => sender ! WordsCounted(toWordCount(countWords.words))
  }

  def toWordCount(words: Array[String]): Map[String, Int] = {
    words.groupBy((word: String) => word.toLowerCase).mapValues(_.length)
  }
}