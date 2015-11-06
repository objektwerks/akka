package event

import java.time.LocalTime

sealed trait Event {
  def occurred: LocalTime = LocalTime.now()
  def batch: Int
}

final case class Brewing(batch: Int) extends Event
final case class Brewed(batch: Int) extends Event

final case class Mashing(batch: Int) extends Event
final case class Mashed(batch: Int) extends Event

final case class Boiling(batch: Int) extends Event
final case class Boiled(batch: Int) extends Event

final case class Cooling(batch: Int) extends Event
final case class Cooled(batch: Int) extends Event

final case class Fermenting(batch: Int) extends Event
final case class Fermented(batch: Int) extends Event

final case class Conditioning(batch: Int) extends Event
final case class Conditioned(batch: Int) extends Event

final case class Casking(batch: Int) extends Event
final case class Casked(batch: Int) extends Event

final case class Kegging(batch: Int) extends Event
final case class Kegged(batch: Int) extends Event

final case class Bottling(batch: Int) extends Event
final case class Bottled(batch: Int) extends Event