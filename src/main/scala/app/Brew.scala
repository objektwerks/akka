package app

import java.time.LocalDateTime

case class Brew(recipe: Recipe,
                initiated: LocalDateTime = LocalDateTime.now(),
                completed: LocalDateTime = LocalDateTime.now())