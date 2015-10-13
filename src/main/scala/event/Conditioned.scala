package event

import java.time.LocalTime

case class Conditioned(number: Int, completed: LocalTime) extends Event