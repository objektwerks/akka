package event

import java.time.LocalTime

case class Brewing(batch: Int, completed: LocalTime) extends Event