package words

import akka.actor.{Actor, Props}

import scala.concurrent.duration._

class Broker extends Actor {
  val queue = context.actorOf(Props[Queue], name = "queue")
  val coordinator = context.actorOf(Props(new Coordinator(self)), name = "coordinator")

  import context.dispatcher
  context.system.scheduler.scheduleOnce(3 seconds, queue, WorkRquest)

  override def receive: Receive = {
    case request: Request => coordinator ! request
    case notification: Notification => queue ! notification
    case response: Response =>
      queue ! response
      queue ! WorkRquest
  }
}