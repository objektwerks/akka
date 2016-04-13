package words

import java.time.LocalDateTime
import java.util.UUID

import scala.collection.mutable

final case class Id(uuid: String = UUID.randomUUID.toString)

sealed trait State
final case class WorkerUnavailable(countWords: CountWords) extends State

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
  def id: Id
  def assigned: LocalDateTime
  def completed: LocalDateTime = LocalDateTime.now
}
final case class WordsCounted(id: Id, assigned: LocalDateTime, words: Array[String], counts: Map[String, Int]) extends Event
object WordsCounted {
  def apply(countWords: CountWords, counts: Map[String, Int]): WordsCounted = {
    WordsCounted(countWords.id, countWords.assigned, countWords.words, counts)
  }
}