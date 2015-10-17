package actor

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Props, Actor, ActorRef}
import command.{Brew, Command}
import domain.Recipe
import event.{Brewing, Event}
import simulator.Simulator
import system.Brewery

class Brewer() extends Actor {
  val system = context.system
  val bottler: ActorRef = system.actorOf(Props[Bottler], name = "bottler")
  val kegger: ActorRef = system.actorOf(Props[Kegger], name = "kegger")
  val casker: ActorRef = system.actorOf(Props[Casker], name = "casker")
  val conditioner: ActorRef = system.actorOf(Props(new Conditioner(bottler, kegger, casker)), name = "conditioner")
  val fermenter: ActorRef = system.actorOf(Props(new Fermenter(conditioner)), name = "fermenter")
  val cooler: ActorRef = system.actorOf(Props(new Cooler(fermenter)), name = "cooler")
  val boiler: ActorRef = system.actorOf(Props(new Boiler(cooler)), name = "boiler")
  val masher: ActorRef = system.actorOf(Props(new Masher(boiler)), name = "masher")
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