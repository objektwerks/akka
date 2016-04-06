package simulation.actor

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorRef}
import simulation.command.{Brew, Command}
import simulation.domain.Recipe
import simulation.event.Event
import simulation.state.State
import simulation.system.Brewery

class Brewer(masher: ActorRef) extends Actor {
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