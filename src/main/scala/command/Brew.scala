package command

import java.time.LocalDateTime

import domain.Recipe

case class Brew(number: Int,
                initiated: LocalDateTime,
                recipe: Recipe)