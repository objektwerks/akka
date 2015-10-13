package event

import java.time.LocalTime

case class Fermenting(batch: Int, completed: LocalTime) extends Event