package event

import java.time.LocalDateTime

case class Brewed(batch: Int,
                  initiated: LocalDateTime,
                  completed: LocalDateTime) {
  def name: String = "Brewed"
}