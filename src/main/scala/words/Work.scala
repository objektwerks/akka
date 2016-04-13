package words

import java.time.LocalDateTime
import java.util.UUID

final case class Request(uuid: String = UUID.randomUUID.toString, words: Array[String] = Words.words)

final case class Response(uuid: String, assigned: LocalDateTime, completed: LocalDateTime, words: Array[String], counts: Map[String, Int])

case object RegisterWorker

sealed trait State
final case class WorkerUnavailable(countWords: CountWords) extends State

sealed trait Command {
  def uuid: String
  def assigned: LocalDateTime = LocalDateTime.now
}
final case class CountWords(uuid: String, words: Array[String]) extends Command

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