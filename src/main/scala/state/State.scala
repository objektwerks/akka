package state

import java.time.LocalTime

sealed trait State {
  def started: LocalTime = LocalTime.now()
  def batch: Int
}
final case class Brewing(batch: Int) extends State
final case class Mashing(batch: Int) extends State
final case class Boiling(batch: Int) extends State
final case class Cooling(batch: Int) extends State
final case class Fermenting(batch: Int) extends State
final case class Conditioning(batch: Int) extends State
final case class Casking(batch: Int) extends State
final case class Kegging(batch: Int) extends State
final case class Bottling(batch: Int) extends State