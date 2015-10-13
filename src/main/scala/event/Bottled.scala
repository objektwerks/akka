package event

import java.time.LocalTime

case class Bottled(number: Int, completed: LocalTime) extends Event