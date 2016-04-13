package words

import java.time.LocalDateTime

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