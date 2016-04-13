package words

import java.time.LocalDateTime
import java.util.UUID

sealed trait State
final case class WorkerUnavailable(countWords: CountWords) extends State

sealed trait Command {
  def uuid: String = UUID.randomUUID.toString
  def assigned: LocalDateTime = LocalDateTime.now
}
case object RegisterWorker extends Command
final case class CountWords(words: Array[String]) extends Command

sealed trait Event {
  def uuid: String
  def assigned: LocalDateTime
  def completed: LocalDateTime = LocalDateTime.now
}
final case class WordsCounted(uuid: String, assigned: LocalDateTime, words: Array[String], counts: Map[String, Int]) extends Event
object WordsCounted {
  def apply(countWords: CountWords, counts: Map[String, Int]): WordsCounted = {
    WordsCounted(countWords.uuid, countWords.assigned, countWords.words, counts)
  }
}