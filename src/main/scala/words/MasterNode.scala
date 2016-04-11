package words

import akka.actor.Props
import akka.util.Timeout
import cluster.Node

import scala.io.Source
import scala.concurrent.duration._

object MasterNode extends Node {
  val words = Source.fromInputStream(getClass.getResourceAsStream("/license.mit")).mkString.split("\\P{L}+")
  val (left, right) = words.splitAt(words.size / 2)

  val master = system.actorOf(Props[Master], name = "master")

  implicit val ec = system.dispatcher
  system.scheduler.schedule(2 seconds, 2 seconds) {
    implicit val timeout = Timeout(3 seconds)
    master ! CountWords(left)
    master ! CountWords(right)
  }
}