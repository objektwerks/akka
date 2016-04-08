package words

import akka.actor.{Actor, ActorLogging}

import scala.io.Source

class Master extends Actor with ActorLogging {
  val words = Source.fromInputStream(getClass.getResourceAsStream("/license.mit")).mkString.split("\\P{L}+")

  def receive = {
    case readyForWork: ReadyForWork => sender ! CountWords(words)
    case wordsCounted: WordsCounted => context.system.log.info(wordsCounted.toString)
  }
}