package words

import scala.collection.mutable.ArrayBuffer

final case class Request(words: List[List[String]])

final case class Response(count: Map[String, Int], fault: Option[String] = None)

final case class CountWordsList(list: List[CountWords]) {
  def size = list.size
}

final case class CountWords(words: List[String]) {
  def count: Map[String, Int] = words.groupBy((word: String) => word.toLowerCase).mapValues(_.length).map(identity)
}

final case class WordsCounted(count: Map[String, Int]) {
  def merge(bufferOfWordCounts: ArrayBuffer[Map[String, Int]]): Map[String, Int] = {
    bufferOfWordCounts.reduceLeft(mergeMaps(_, _)(_ + _))
  }

  private def mergeMaps[K, V](mapOne: Map[K, V], mapTwo: Map[K, V])(func: (V, V) => V): Map[K, V] = {
    (mapOne -- mapTwo.keySet) ++
      (mapTwo -- mapOne.keySet) ++
      (for (k <- mapOne.keySet & mapTwo.keySet) yield {
        k -> func(mapOne(k), mapTwo(k))
      })
  }
}