package words

import akka.actor.{Actor, ActorLogging}

import scala.io.Source

class Queue extends Actor with ActorLogging {
  val list = Source.fromInputStream(getClass.getResourceAsStream("/license.mit")).mkString.split("\\P{L}+").toList
  val words = list.grouped(list.length / 8).toList // list of length 168 / 8 = 21 words per sub list

  override def receive: Receive = {
    case WorkRquest =>
      log.info(s"Queue received work request from ${sender.path.name}")
      sender ! Request(Id(), Words(words))
    case response: PartialResponse => log.info(s"Queue received a partial response[id: ${response.id}]")
    case response: Response => log.info(s"Queue received response[id: ${response.id}]")
  }
}