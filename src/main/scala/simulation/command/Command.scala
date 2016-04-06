package simulation.command

import java.time.LocalTime

import simulation.domain.Recipe

sealed trait Command {
  def executed: LocalTime = LocalTime.now()
  def batch: Int
}
final case class Brew(batch: Int, recipe: Recipe) extends Command