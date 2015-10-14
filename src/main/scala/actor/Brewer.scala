package actor

import java.time.LocalTime
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, ActorRef}
import command.{Brew, Command}
import domain.Recipe
import event.{Brewing, Event}
import simulator.Simulator
import system.Brewery

class Brewer(masher: ActorRef) extends Actor with ActorLogging {
  val batchNumber = new AtomicInteger()
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case recipe: Recipe =>
      val brew = Brew(batchNumber.incrementAndGet(), recipe)
      publisher.publish(brew)
      Simulator.simulate()
      publisher.publish(Brewing(brew.batch))
      masher ! brew
    case command: Command => Brewery.command(command)
    case event: Event => Brewery.event(event)
  }
}