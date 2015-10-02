package event

import java.time.LocalDateTime

import domain.Recipe

case class Brewed(number: Int,
                  initiated: LocalDateTime,
                  completed: LocalDateTime,
                  recipe: Recipe)