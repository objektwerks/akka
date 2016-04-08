package words

import java.time.LocalTime

// State
final case class ReadyForWork(at: LocalTime = LocalTime.now)

sealed trait Work {
  def assigned: LocalTime = LocalTime.now
  def completed: Option[LocalTime]
}

// Command
final case class CountWords(words: Array[String], completed: Option[LocalTime] = Some(LocalTime.now)) extends Work

// Event
final case class WordsCounted(counts: Map[String, Int], completed: Option[LocalTime] = Some(LocalTime.now)) extends Work