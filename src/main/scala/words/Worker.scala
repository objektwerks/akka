package words

import akka.actor.{Actor, ActorLogging}

class Worker extends Actor with ActorLogging {
  override def receive: Receive = {
    case countWords: CountWords =>
      log.info(s"Received $countWords from master: ${sender.path.name}")
      sender ! WordsCounted(countWords, countWords.count)
  }
}