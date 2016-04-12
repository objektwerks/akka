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
  private val commands: mutable.ListMap[Id, Command] = mutable.ListMap.empty[Id, Command]

  def nonEmpty: Boolean = commands.nonEmpty
  def head: Command = {
    val (_, command) = commands.head
    add(command)
    command
  }
  def add(command: Command): Unit = commands += command.id -> command
  def remove(id: Id): Unit = commands -= id
}

sealed trait Event {
  def commandId: Id
  def completed: LocalDateTime = LocalDateTime.now
}
final case class WordsCounted(commandId: Id, counts: Map[String, Int]) extends Event