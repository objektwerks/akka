package words

import java.time.{Duration, LocalDateTime}
import java.util.UUID

final case class Words(list: List[List[String]]) {
  def size: Int = list.size
}

final case class Id(uuid: String = UUID.randomUUID.toString,
                    received: LocalDateTime = LocalDateTime.now,
                    completed: Option[LocalDateTime] = None,
                    duration: Option[Duration] = None,
                    totalDuration: Option[Duration] = None) {
  def toUpdatedCopy: Id = {
    val newCompleted = LocalDateTime.now
    val newDuration = toDuration(received, newCompleted)
    val newTotalDuration = toTotalDuration(totalDuration, newDuration)
    copy(completed = Some(newCompleted), duration = Some(newDuration), totalDuration = Some(newTotalDuration))
  }

  private def toDuration(from: LocalDateTime, to: LocalDateTime): Duration = Duration.between(from, to)

  private def toTotalDuration(runningTotalDuration: Option[Duration], newDuration: Duration): Duration = {
    if (runningTotalDuration.nonEmpty) runningTotalDuration.get.plus(newDuration) else newDuration
  }
}

final case class Request(id: Id, words: Words)

case object WorkRquest

final case class Notification(id: Id, part: Int, of: Int, wordsCounted: Map[String, Int])

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