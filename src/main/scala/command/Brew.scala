package command

import java.time.LocalTime

import domain.Recipe

case class Brew(number: Int, initiated: LocalTime, recipe: Recipe) extends Command