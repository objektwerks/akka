package app

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

import akka.actor._
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

class Brewery(batchEventListener: BatchEventListener) {
  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  val batchNumber = new AtomicInteger()

  val system: ActorSystem = ActorSystem.create("Brewery", ConfigFactory.load("brewery.conf"))
  val bottler: ActorRef = system.actorOf(Props[Bottler], name = "bottler")
  val conditioner: ActorRef = system.actorOf(Props(new Conditioner(bottler)), name = "conditioner")
  val fermenter: ActorRef = system.actorOf(Props(new Fermenter(conditioner)), name = "fermenter")
  val cooler: ActorRef = system.actorOf(Props(new Cooler(fermenter)), name = "cooler")
  val boiler: ActorRef = system.actorOf(Props(new Boiler(cooler)), name = "boiler")
  val masher: ActorRef = system.actorOf(Props(new Masher(boiler)), name = "masher")
  val brewer: ActorRef = system.actorOf(Props(new Brewer(masher)), name = "brewer")

  val listener: ActorRef = system.actorOf(Props(new BatchListener(batchEventListener)), name = "listener")
  system.eventStream.subscribe(listener, classOf[Batch])

  def brew(recipe: Recipe): Unit = {
    brewer ! Batch(batchNumber.incrementAndGet(), LocalDateTime.now, LocalDateTime.now, recipe)
  }
}