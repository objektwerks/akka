package words

import akka.actor.{Actor, ActorLogging}

class Worker extends Actor with ActorLogging {
  override def receive: Receive = {
    case countWords: CountWords =>
      log.info(s"Worker eceived $countWords from master: ${sender.path.name}, sent count words to Master.")
      sender ! WordsCounted(countWords, countWords.count)
  }
}