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
    case response: PartialResponse =>
      val statement = s"Broker received a partial response[count: ${response.wordsCounted.size}, part: ${response.part} of: ${response.of}]: $response"
      log.info(statement)
      queue ! response
    case response: Response =>
      log.info(s"Broker received response[count = ${response.wordsCounted.size}]: $response")
      queue ! response
      queue ! WorkRquest
  }
}