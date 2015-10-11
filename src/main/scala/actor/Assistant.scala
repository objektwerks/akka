package actor

import akka.actor.{Actor, ActorLogging, ActorRef}
import command.Brew
import simulator.Simulator

class Assistant(masher: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case brew: Brew =>
      Simulator.simulate(39)
      masher ! brew
  }
}