package words

import java.time.LocalDateTime
import java.util.UUID

sealed trait Message

final case class Request(uuid: String = UUID.randomUUID.toString, words: Array[String] = Words.words) extends Message

final case class Response(uuid: String, assigned: LocalDateTime, completed: LocalDateTime, words: Array[String], counts: Map[String, Int]) extends Message

object Response {
  def apply(wordsCounted: WordsCounted): Response = {
    Response(wordsCounted.uuid, wordsCounted.assigned, wordsCounted.completed, wordsCounted.words, wordsCounted.counts)
  }
}

case object RegisterWorker extends Message