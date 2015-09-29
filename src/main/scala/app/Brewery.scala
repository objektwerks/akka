package app

import java.time.LocalDateTime

import akka.actor._

case class Batch(initiated: LocalDateTime,
                 completed: LocalDateTime,
                 recipe: Recipe)

class BatchListener extends Actor {
  def receive = {
    case batch: Batch =>
  }
}

class Brewery {
  private val system: ActorSystem = ActorSystem.create("Brewery")
  private val brewer: ActorRef = system.actorOf(Props[Brewer], name = "brewer")
  private val listener: ActorRef = system.actorOf(Props[BatchListener], name = "listener")
  system.eventStream.subscribe(listener, classOf[Batch])

  def brew(recipe: Recipe): Unit = {
    brewer ! Batch(LocalDateTime.now, LocalDateTime.now, recipe)
  }
}