package app

import java.time.LocalDateTime

import akka.actor.Actor

case class Batch(initiated: LocalDateTime,
                 completed: LocalDateTime,
                 recipe: Recipe)

class BatchListener(batchEventListener: BatchEventListener) extends Actor {
  def receive = {
    case batch: Batch => batchEventListener.onEvent(batch)
  }
}

class BatchEventListener {
  def onEvent(batch: Batch): Unit = {

  }
}