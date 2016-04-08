package words

import akka.actor.{Actor, ActorLogging}
import akka.util.Timeout

import scala.concurrent.duration._

class Worker extends Actor with ActorLogging {
  implicit val ec = context.system.dispatcher
  context.system.scheduler.schedule(3 seconds, 3 seconds) {
    implicit val timeout = Timeout(3 seconds)
    // Need actor ref to Master.
  }

  def receive = {
    case countWords: CountWords => sender ! WordsCounted(toWordCount(countWords.words))
  }

  def toWordCount(words: Array[String]): Map[String, Int] = {
    words.groupBy((word: String) => word.toLowerCase).mapValues(_.length)
  }
}