package actor

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{ActorRef, Actor}
import command.{Brew, Command}
import domain.Recipe
import event.{Brewing, Event}
import system.Brewery

class Brewer(masher: ActorRef) extends Actor {
  val batchNumber = new AtomicInteger()
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case recipe: Recipe =>
      val brew = Brew(batchNumber.incrementAndGet(), recipe)
      publisher.publish(brew)
      publisher.publish(Brewing(brew.batch))
      masher ! brew
    case command: Command => Brewery.command(command)
    case event: Event => Brewery.event(event)
  }
}