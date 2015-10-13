package actor

import java.time.LocalTime
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, ActorRef}
import command.{Brew, Command}
import domain.Recipe
import event.Event
import system.Brewery

class Brewer(masher: ActorRef) extends Actor with ActorLogging {
  val batchNumber = new AtomicInteger()

  override def receive: Receive = {
    case recipe: Recipe => masher ! Brew(batchNumber.incrementAndGet(), LocalTime.now, recipe)
    case command: Command => Brewery.command(command)
    case event: Event => Brewery.event(event)
  }
}