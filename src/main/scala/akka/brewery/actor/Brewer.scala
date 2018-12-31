package akka.brewery.actor

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.brewery._

class Brewer(masher: ActorRef) extends Actor with ActorLogging {
  val batchNumber = new AtomicInteger()
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case recipe: Recipe =>
      val brew = Brew(batchNumber.incrementAndGet(), recipe)
      publisher.publish(brew)
      masher ! brew
    case command: Command => Brewery.onCommand(command)
    case state: State => Brewery.onState(state)
    case event: Event => Brewery.onEvent(event)
  }
}