package words

import java.time.LocalTime

final case class WorkRequest(at: LocalTime = LocalTime.now)

sealed trait Work {
  def assigned: LocalTime = LocalTime.now
  def completed: Option[LocalTime]
}
final case class CountWords(words: Array[String], completed: Option[LocalTime] = Some(LocalTime.now)) extends Work
final case class WordsCounted(counts: Map[String, Int], completed: Option[LocalTime] = Some(LocalTime.now)) extends Work