package event

import java.time.LocalDateTime

case class Fermented(number: Int, completed: LocalDateTime) extends Event {
  def name: String = "Fermented"
}