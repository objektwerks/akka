package words

import akka.actor.{Actor, ActorLogging}

// Make persistent.
class Master extends Actor with ActorLogging {
  val countWords = Seq.empty[CountWords]

  def receive = {
    case workRequest: CountWords => countWords :+ workRequest
    case readyForWork: ReadyForWork if countWords.nonEmpty => sender ! countWords.head
    case wordsCounted: WordsCounted => context.system.log.info(wordsCounted.toString)
  }
}