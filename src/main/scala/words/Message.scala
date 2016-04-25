package words

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

final case class Words(list: List[List[String]]) {
  def size: Int = list.size
}

final case class Id(uuid: String = UUID.randomUUID.toString,
                    received: LocalDateTime = LocalDateTime.now,
                    completed: Option[LocalDateTime] = None,
                    duration: Option[String] = None) {
  def toCopy(id: Id): Id = {
    val onCompleted = LocalDateTime.now
    id.copy(completed = Some(onCompleted), duration = Some(id.toDuration(id.received, onCompleted)))
  }

  private def toDuration(from: LocalDateTime, to: LocalDateTime): String = {
    var temp = LocalDateTime.from(from)
    val hours = temp.until(to, ChronoUnit.HOURS)
    temp = temp.plusHours(hours)
    val minutes = temp.until(to, ChronoUnit.MINUTES)
    temp = temp.plusMinutes(minutes)
    val seconds = temp.until(to, ChronoUnit.SECONDS)
    val millis = temp.until(to, ChronoUnit.MILLIS)
    s"Hours: $hours, Minutes: $minutes, Seconds: $seconds, Millis: $millis"
  }
}

final case class Request(id: Id, words: Words)

case object WorkRquest

final case class PartialResponse(id: Id, part: Int, of: Int, wordsCounted: Map[String, Int])

final case class Response(id: Id, wordsCounted: Map[String, Int], error: Option[String] = None)

final case class CountWords(words: List[String]) {
  def count: Map[String, Int] = words.groupBy((word: String) => word.toLowerCase).mapValues(_.length).map(identity)
}

final case class WordsCounted(count: Map[String, Int])

object WordsCounted {
  def merge(wordCounts: IndexedSeq[Map[String, Int]]): Map[String, Int] = {
    wordCounts.reduceLeft(merge(_, _)(_ + _))
  }

  private def merge[K, V](firstMap: Map[K, V], secondMap: Map[K, V])(func: (V, V) => V): Map[K, V] = {
    (firstMap -- secondMap.keySet) ++
      (secondMap -- firstMap.keySet) ++
      (for (key <- firstMap.keySet & secondMap.keySet) yield {
        key -> func(firstMap(key), secondMap(key))
      })
  }
}

final case class PartialWordsCounted(partialCount: Map[String, Int], cause: String)