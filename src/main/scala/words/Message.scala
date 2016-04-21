package words

final case class Words(list: List[List[String]]) {
  def size: Int = list.size
}

final case class Request(words: Words)

final case class PartialResponse(part: Int, of: Int, count: Map[String, Int])

final case class Response(count: Map[String, Int], error: Option[String] = None)

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