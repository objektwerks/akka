package words

import akka.actor.{Actor, ActorLogging, Props}

import scala.concurrent.duration._

class Broker extends Actor with ActorLogging {
  val queue = context.actorOf(Props[Queue], name = "queue")
  val coordinator = context.actorOf(Props(new Coordinator(self)), name = "coordinator")

  import context.dispatcher
  context.system.scheduler.scheduleOnce(3 seconds, queue, WorkRquest)

  override def receive: Receive = {
    case request: Request =>
      log.info(s"Broker received request[partitions = ${request.words.size}]: $request")
      coordinator ! request
    case notification: Notification =>
      val statement = s"Broker received notification[count: ${notification.wordsCounted.size}," +
        s" part: ${notification.part} of: ${notification.of}]: $notification"
      log.info(statement)
      queue ! notification
    case response: Response =>
      log.info(s"Broker received response[count = ${response.wordsCounted.size}]: $response")
      queue ! response
      queue ! WorkRquest
  }
}