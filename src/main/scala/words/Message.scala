package words

import java.time.LocalDateTime
import java.util.UUID

final case class Request(uuid: String = UUID.randomUUID.toString, words: List[List[String]] = Words.words)

final case class Response(uuid: String, assigned: LocalDateTime, completed: LocalDateTime, words: List[String], counts: Map[String, Int])

object Response {
  def apply(wordsCounted: WordsCounted): Response = {
    Response(wordsCounted.uuid, wordsCounted.assigned, wordsCounted.completed, wordsCounted.words, wordsCounted.counts)
  }
}

final case class ListOfCountWords(list: List[CountWords])

sealed trait Command {
  def uuid: String
  def assigned: LocalDateTime = LocalDateTime.now
}

final case class CountWords(uuid: String, words: List[String]) extends Command

sealed trait Event {
  def uuid: String
  def assigned: LocalDateTime
  def completed: LocalDateTime = LocalDateTime.now
}

final case class WordsCounted(uuid: String, assigned: LocalDateTime, words: List[String], counts: Map[String, Int]) extends Event

object WordsCounted {
  def apply(countWords: CountWords, counts: Map[String, Int]): WordsCounted = {
    WordsCounted(countWords.uuid, countWords.assigned, countWords.words, counts)
  }
}