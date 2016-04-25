package words

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import scala.concurrent.duration._

class Broker(queue: ActorRef) extends Actor with ActorLogging {
  val coordinator = context.actorOf(Props(new Coordinator(self)), name = "coordinator")

  import context.dispatcher
  context.system.scheduler.scheduleOnce(1 second, queue, WorkRquest)

  override def receive: Receive = {
    case request: Request =>
      log.info(s"Broker received request[partitions = ${request.words.size}]: $request")
      coordinator ! request
    case response: Response =>
      log.info(s"Broker received response[count = ${response.wordsCounted.size}]: $response")
      queue ! WorkRquest
    case response: PartialResponse =>
      val statement = s"Broker received a partial response[count: ${response.wordsCounted.size}, part: ${response.part} of: ${response.of}]: $response"
      log.info(statement)
  }
}