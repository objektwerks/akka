package command

import java.time.LocalTime

import domain.Recipe

sealed trait Command {
  def initiated: LocalTime = LocalTime.now()
}
case class Brew(batch: Int, recipe: Recipe) extends Command