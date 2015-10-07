package brewery

import java.time.LocalDateTime

import akka.actor.Actor
import command.Brew
import event.Brewed

class Bottler extends Actor {
  override def receive: Receive = {
    case brew: Brew => context.system.eventStream.publish(Brewed(brew.number, brew.initiated, LocalDateTime.now, brew.recipe))
  }
}