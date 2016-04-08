package words

import java.time.LocalTime

sealed trait Work {
  def assigned: LocalTime = LocalTime.now
  def completed: LocalTime
}
final case class CountWords(words: Array[String], completed: LocalTime = LocalTime.now) extends Work
final case class WordsCounted(counts: Map[String, Int], completed: LocalTime = LocalTime.now) extends Work