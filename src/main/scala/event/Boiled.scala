package event

import java.time.LocalDateTime

case class Boiled(batch: Int, completed: LocalDateTime) extends Event {
  def name: String = "Boilded"
}