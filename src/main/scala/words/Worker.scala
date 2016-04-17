package words

import akka.actor.{Actor, ActorLogging}

class Worker extends Actor with ActorLogging {
  override def receive: Receive = {
    case countWords: CountWords =>
      log.info(s"Worker [${self.path.name}] received / replied $countWords [${countWords.words.size}] from / to Master [${sender.path.name}].")
      sender ! WordsCounted(countWords, countWords.count)
  }
}