package command

import java.time.LocalTime

import domain.Recipe

sealed trait Command
case class Brew(batch: Int, initiated: LocalTime, recipe: Recipe) extends Command