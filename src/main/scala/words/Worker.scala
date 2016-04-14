package words

import akka.actor.{Actor, ActorLogging}

class Worker extends Actor with ActorLogging {
  override def receive: Receive = {
    case countWords: CountWords => sender ! WordsCounted(countWords, countWords.count)
  }
}