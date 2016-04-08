package words

import akka.actor.{Actor, ActorLogging}

class Master extends Actor with ActorLogging {
  def receive = {
    case countWords: CountWords => sender ! countWords
  }
}