package event

import java.time.LocalTime

case class Bottling(batch: Int, completed: LocalTime) extends Event