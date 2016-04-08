package words

import akka.actor.{Actor, ActorLogging}

class Worker extends Actor with ActorLogging {
  def receive = {
    case words: Words => sender ! WordCounts(toWordCount(words.array))
  }

  def toWordCount(words: Array[String]): Map[String, Int] = {
    words.groupBy((word: String) => word.toLowerCase).mapValues(_.length)
  }
}