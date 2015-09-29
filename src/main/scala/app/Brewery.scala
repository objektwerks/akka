package app

import java.time.LocalDateTime

import akka.actor._

class Brewery(batchEventListener: BatchEventListener) {
  private val system: ActorSystem = ActorSystem.create("Brewery")
  private val brewer: ActorRef = system.actorOf(Props[Brewer], name = "brewer")
  private val listener: ActorRef = system.actorOf(Props(new BatchListener(batchEventListener)), name = "listener")
  system.eventStream.subscribe(listener, classOf[Batch])

  def brew(recipe: Recipe): Unit = {
    brewer ! Batch(LocalDateTime.now, LocalDateTime.now, recipe)
  }
}