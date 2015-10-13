package event

import java.time.LocalTime

case class Mashed(number: Int, completed: LocalTime) extends Event