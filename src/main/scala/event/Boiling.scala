package event

import java.time.LocalTime

case class Boiling(batch: Int, completed: LocalTime) extends Event