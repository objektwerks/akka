package event

import java.time.LocalDateTime

case class Boiled(batch: Int, completed: LocalDateTime) extends Stage