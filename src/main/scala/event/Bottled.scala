package event

import java.time.LocalDateTime

case class Bottled(number: Int, initiated: LocalDateTime, completed: LocalDateTime) extends Event