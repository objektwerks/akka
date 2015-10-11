package actor

import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import command.Brew
import domain.Recipe
import event.{Brewed, Stage}
import system.Brewery

class Brewer(masher: ActorRef) extends Actor with ActorLogging {
  val batchNumber = new AtomicInteger()
  var router = {
    val routees = Vector.fill(3) {
      val assistant = context.actorOf(Props(new Assistant(masher)), name = "assistant")
      context watch assistant
      ActorRefRoutee(assistant)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  override def receive: Receive = {
    case recipe: Recipe => router.route(Brew(batchNumber.incrementAndGet(), LocalDateTime.now, recipe), sender)
    case stage: Stage => Brewery.stage(stage)
    case brewed: Brewed => Brewery.brewed(brewed)
  }
}