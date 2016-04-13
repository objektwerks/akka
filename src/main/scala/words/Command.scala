package words

import java.time.LocalDateTime

sealed trait Command {
  def uuid: String
  def assigned: LocalDateTime = LocalDateTime.now
}

final case class CountWords(uuid: String, words: Array[String]) extends Command