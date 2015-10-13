package event

import java.time.LocalTime

sealed trait Event
case class Boiling(batch: Int, completed: LocalTime) extends Event
case class Boiled(batch: Int, completed: LocalTime) extends Event
case class Brewing(batch: Int, completed: LocalTime) extends Event
case class Brewed(batch: Int, initiated: LocalTime, completed: LocalTime) extends Event
case class Casking(batch: Int, completed: LocalTime) extends Event
case class Casked(number: Int, completed: LocalTime) extends Event
case class Conditioning(batch: Int, completed: LocalTime) extends Event
case class Conditioned(number: Int, completed: LocalTime) extends Event
case class Cooling(batch: Int, completed: LocalTime) extends Event
case class Cooled(number: Int, completed: LocalTime) extends Event
case class Fermenting(batch: Int, completed: LocalTime) extends Event
case class Fermented(number: Int, completed: LocalTime) extends Event
case class Mashing(batch: Int, completed: LocalTime) extends Event
case class Mashed(number: Int, completed: LocalTime) extends Event