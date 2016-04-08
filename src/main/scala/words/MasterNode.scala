package words

import akka.actor.Props
import akka.util.Timeout
import akka.pattern._
import cluster.Node

import scala.io.Source
import scala.concurrent.duration._

object MasterNode extends Node {
  val words = Source.fromInputStream(getClass.getResourceAsStream("/license.mit")).mkString.split("\\P{L}+")
  val (left, right) = words.splitAt(words.size / 2)

  val master = system.actorOf(Props[Master], name = "master")

  implicit val ec = system.dispatcher
  system.scheduler.schedule(3 seconds, 3 seconds) {
    implicit val timeout = Timeout(3 seconds)
    (master ? CountWords(left)) onSuccess { case wordsCounted: WordsCounted => system.log.info(wordsCounted.toString) }
    (master ? CountWords(right)) onSuccess { case wordsCounted: WordsCounted => system.log.info(wordsCounted.toString) }
  }
}