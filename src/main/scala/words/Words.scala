package words

import scala.io.Source

object Words {
  val array = Source.fromInputStream(getClass.getResourceAsStream("/license.mit")).mkString.split("\\P{L}+")
  val (left, right) = array.splitAt(array.size / 2)

  def toWordCount(words: Array[String]): Map[String, Int] = {
    words.groupBy((word: String) => word.toLowerCase).mapValues(_.length)
  }
}