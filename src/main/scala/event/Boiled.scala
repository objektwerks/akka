package event

import java.time.LocalTime

case class Boiled(batch: Int, completed: LocalTime) extends Event