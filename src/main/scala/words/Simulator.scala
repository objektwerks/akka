package words

import akka.actor.{Actor, ActorLogging, ActorRef}


class Simulator(listener: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case words: Words => listener ! Request(Id(), words)
  }
}