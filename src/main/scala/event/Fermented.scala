package event

import java.time.LocalTime

case class Fermented(number: Int, completed: LocalTime) extends Event