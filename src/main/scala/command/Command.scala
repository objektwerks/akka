package command

import java.time.LocalTime

import domain.Recipe

sealed trait Command {
  def executed: LocalTime = LocalTime.now()
  def batch: Int
}
case class Brew(batch: Int, recipe: Recipe) extends Command