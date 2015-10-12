package event

import java.time.LocalDateTime

case class Cooled(number: Int, completed: LocalDateTime) extends Event {
  def name: String = "Cooled"
}