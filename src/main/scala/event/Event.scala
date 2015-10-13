package event

import java.time.LocalTime

sealed trait Event

case class Boiling(batch: Int, completed: LocalTime) extends Event
case class Boiled(batch: Int, completed: LocalTime) extends Event

case class Brewing(batch: Int, completed: LocalTime) extends Event
case class Brewed(batch: Int, initiated: LocalTime, completed: LocalTime) extends Event

case class Casking(batch: Int, completed: LocalTime) extends Event
case class Casked(batch: Int, completed: LocalTime) extends Event

case class Conditioning(batch: Int, completed: LocalTime) extends Event
case class Conditioned(batch: Int, completed: LocalTime) extends Event

case class Cooling(batch: Int, completed: LocalTime) extends Event
case class Cooled(batch: Int, completed: LocalTime) extends Event

case class Fermenting(batch: Int, completed: LocalTime) extends Event
case class Fermented(batch: Int, completed: LocalTime) extends Event

case class Mashing(batch: Int, completed: LocalTime) extends Event
case class Mashed(batch: Int, completed: LocalTime) extends Event