package event

import java.time.LocalTime

case class Cooling(batch: Int, completed: LocalTime) extends Event