package event

import java.time.LocalDateTime

case class Mashed(number: Int, completed: LocalDateTime) extends Event {
  def name: String = "Mashed"
}