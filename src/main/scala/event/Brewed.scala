package event

import java.time.LocalTime

case class Brewed(batch: Int, initiated: LocalTime, completed: LocalTime) extends Event