package words

import java.time.LocalTime

sealed trait Message {
  def created: LocalTime = LocalTime.now()
}
final case class Words(array: Array[String]) extends Message
final case class WordCounts(map: Map[String, Int]) extends Message
