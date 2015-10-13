package event

import java.time.LocalTime

case class Mashing(batch: Int, completed: LocalTime) extends Event