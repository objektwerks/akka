package event

import java.time.LocalTime

case class Casking(batch: Int, completed: LocalTime) extends Event