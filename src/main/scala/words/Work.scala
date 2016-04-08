package words

import java.time.LocalTime

sealed trait Work {
  def assigned: LocalTime = LocalTime.now
  def completed: LocalTime
}
final case class Words(array: Array[String], completed: LocalTime = LocalTime.now) extends Work
final case class WordCounts(map: Map[String, Int], completed: LocalTime = LocalTime.now) extends Work