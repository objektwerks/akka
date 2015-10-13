package event

import java.time.LocalTime

case class Conditioning(batch: Int, completed: LocalTime) extends Event