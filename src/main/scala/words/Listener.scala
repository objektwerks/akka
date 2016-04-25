package words

import akka.actor.{Actor, ActorLogging, Props}

class Listener extends Actor with ActorLogging {
  val coordinator = context.actorOf(Props(new Coordinator(self)), name = "coordinator")

  override def receive: Receive = {
    case request: Request =>
      log.info(s"Listener received request: $request")
      coordinator ! request
    case response: Response => log.info(s"Listener received response[count = ${response.count.size}]: $response")
    case response: PartialResponse =>
      val statement =
        s"""Listener received a partial response[count: ${response.count.size},
           | part: ${response.part} of: ${response.of}]: $response""".stripMargin
      log.info(statement)
  }
}