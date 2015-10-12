package event

import java.time.LocalDateTime

case class Brewed(batch: Int,
                  initiated: LocalDateTime,
                  completed: LocalDateTime) extends Event {
  def name: String = "Brewed"
}