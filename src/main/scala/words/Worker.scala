package words

import akka.actor.{Actor, ActorLogging}

class Worker extends Actor with ActorLogging {
  def receive = {
    case words: String => log.info(words)
  }
}