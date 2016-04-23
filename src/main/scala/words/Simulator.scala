package words

import akka.actor.{Actor, ActorLogging, Props}

import scala.io.Source

class Simulator extends Actor with ActorLogging {
  val list = Source.fromInputStream(getClass.getResourceAsStream("/license.mit")).mkString.split("\\P{L}+").toList
  val words = list.grouped(list.length / 8).toList // list of length 168 / 8 = 21 words per sub list
  val listener = context.system.actorOf(Props[Listener], name = "listener")
  val responder = context.system.actorOf(Props[Responder], name = "responder")
  val coordinator = context.actorOf(Props[Coordinator], name = "coordinator")

  override def receive: Receive = {
    case words: Words => listener ! Request(Id(), words)
  }
}