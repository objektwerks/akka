package event

import java.time.LocalDateTime

import domain.Recipe

case class Brewed(batch: Int,
                  initiated: LocalDateTime,
                  completed: LocalDateTime,
                  recipe: Recipe)