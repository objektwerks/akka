package app

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

import akka.actor._
import akka.util.Timeout

class Brewery(batchEventListener: BatchEventListener) {
  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  val batchNumber = new AtomicInteger()
  val system: ActorSystem = ActorSystem.create("Brewery")
  val brewer: ActorRef = system.actorOf(Props[Brewer], name = "brewer")
  val listener: ActorRef = system.actorOf(Props(new BatchListener(batchEventListener)), name = "listener")
  system.eventStream.subscribe(listener, classOf[Batch])

  def brew(recipe: Recipe): Unit = {
    brewer ! Batch(batchNumber.incrementAndGet(), LocalDateTime.now, LocalDateTime.now, recipe)
  }
}