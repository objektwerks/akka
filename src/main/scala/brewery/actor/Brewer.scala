package brewery.actor

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, ActorRef}
import brewery._

class Brewer(masher: ActorRef) extends Actor with ActorLogging {
  val batchNumber = new AtomicInteger()
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case recipe: Recipe =>
      val brew = Brew(batchNumber.incrementAndGet(), recipe)
      publisher.publish(brew)
      masher ! brew
    case command: Command => Brewery.command(command)
    case state: State => Brewery.state(state)
    case event: Event => Brewery.event(event)
  }
}