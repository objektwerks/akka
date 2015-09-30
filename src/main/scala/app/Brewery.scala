package app

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration._

object Brewery extends App {
  implicit val timeout = new Timeout(10, TimeUnit.SECONDS)
  val log = LoggerFactory.getLogger(this.getClass)

  val system: ActorSystem = ActorSystem.create("Brewery", ConfigFactory.load("brewery.conf"))
  val bottler: ActorRef = system.actorOf(Props[Bottler], name = "bottler")
  val conditioner: ActorRef = system.actorOf(Props(new Conditioner(bottler)), name = "conditioner")
  val fermenter: ActorRef = system.actorOf(Props(new Fermenter(conditioner)), name = "fermenter")
  val cooler: ActorRef = system.actorOf(Props(new Cooler(fermenter)), name = "cooler")
  val boiler: ActorRef = system.actorOf(Props(new Boiler(cooler)), name = "boiler")
  val masher: ActorRef = system.actorOf(Props(new Masher(boiler)), name = "masher")
  val brewer: ActorRef = system.actorOf(Props(new Brewer(masher)), name = "brewer")
  system.eventStream.subscribe(brewer, classOf[Batch])
  log.info("Brewery initialized!")

  sys addShutdownHook {
    log.info("Brewery shutting down...")
    Await.result(system.terminate(), 3 seconds)
    log.info("Brewery shutdown.")
  }
}