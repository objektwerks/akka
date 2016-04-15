package words

import java.time.LocalDateTime
import java.util.UUID

import scala.collection.mutable.ArrayBuffer

final case class Request(uuid: String = UUID.randomUUID.toString, words: List[List[String]])

final case class Response(uuid: String, assigned: LocalDateTime, completed: LocalDateTime, counts: Map[String, Int])

object Response {
  def apply(wordsCounted: WordsCounted): Response = {
    Response(wordsCounted.uuid, wordsCounted.assigned, wordsCounted.completed, wordsCounted.counts)
  }
}

final case class Fault(cause: String)

final case class ListOfCountWords(list: List[CountWords])

sealed trait Command {
  def uuid: String
  def assigned: LocalDateTime = LocalDateTime.now
}

final case class CountWords(uuid: String, words: List[String]) extends Command {
  def count: Map[String, Int] = {
    words.groupBy((word: String) => word.toLowerCase).mapValues(_.length).map(identity)
  }
}

sealed trait Event {
  def uuid: String
  def assigned: LocalDateTime
  def completed: LocalDateTime = LocalDateTime.now
}

final case class WordsCounted(uuid: String, assigned: LocalDateTime, counts: Map[String, Int]) extends Event

object WordsCounted {
  def apply(countWords: CountWords, counts: Map[String, Int]): WordsCounted = {
    WordsCounted(countWords.uuid, countWords.assigned, counts)
  }

  def merge(listOfWordsCounted: ArrayBuffer[WordsCounted]): Map[String, Int] = {
    listOfWordsCounted.groupBy(_.counts.head._1).map { case (k, v) => k -> v.map(_.counts.head._2).sum }
  }
}