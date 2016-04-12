package words

import java.time.LocalDateTime
import java.util.UUID

import scala.collection.mutable

final case class Id(uuid: String = UUID.randomUUID.toString)

sealed trait Command {
  def id: Id = Id()
  def assigned: LocalDateTime = LocalDateTime.now
}
case object RegisterWorker extends Command
final case class CountWords(words: Array[String]) extends Command

final case class Commands() {
  private val commands: mutable.ArraySeq[Command] = mutable.ArraySeq.empty[Command]

  def add(command: Command): Unit = commands :+ command
  def head: Command = commands.head
  def nonEmpty: Boolean = commands.nonEmpty
}

sealed trait Event {
  def commandId: Id
  def completed: LocalDateTime = LocalDateTime.now
}
final case class WordsCounted(commandId: Id, counts: Map[String, Int]) extends Event