package words

import akka.actor.{Actor, ActorLogging, ActorRef}

import scala.io.Source

class Cloud(broker: ActorRef) extends Actor with ActorLogging {
  val list = Source.fromInputStream(getClass.getResourceAsStream("/license.mit")).mkString.split("\\P{L}+").toList
  val words = list.grouped(list.length / 8).toList // list of length 168 / 8 = 21 words per sub list

  override def receive: Receive = {
    case words: Words => broker ! Request(Id(), words)
  }
}