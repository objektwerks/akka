package akka.brewery

import java.time.LocalTime

sealed trait Command {
  def executed: LocalTime = LocalTime.now()
  def batch: Int
}
final case class Brew(batch: Int, recipe: Recipe) extends Command