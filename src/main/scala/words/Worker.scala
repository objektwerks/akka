package words

import akka.actor.{Actor, ActorLogging}

class Worker extends Actor with ActorLogging {
  override def receive: Receive = {
    case countWords: CountWords =>
      log.info(s"Worker [${self.path.name}] received $countWords from Master [${sender.path.name}]. Sent count words to Master.")
      sender ! WordsCounted(countWords, countWords.count)
  }
}