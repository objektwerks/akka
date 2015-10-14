package event

import java.time.LocalTime

sealed trait Event {
  def completed: LocalTime = LocalTime.now()
}

case class Boiling(batch: Int) extends Event
case class Boiled(batch: Int) extends Event

case class Brewing(batch: Int) extends Event
case class Brewed(batch: Int) extends Event

case class Casking(batch: Int) extends Event
case class Casked(batch: Int) extends Event

case class Conditioning(batch: Int) extends Event
case class Conditioned(batch: Int) extends Event

case class Cooling(batch: Int) extends Event
case class Cooled(batch: Int) extends Event

case class Fermenting(batch: Int) extends Event
case class Fermented(batch: Int) extends Event

case class Mashing(batch: Int) extends Event
case class Mashed(batch: Int) extends Event