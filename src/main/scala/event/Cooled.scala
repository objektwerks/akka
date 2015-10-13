package event

import java.time.LocalTime

case class Cooled(number: Int, completed: LocalTime) extends Event