package app

import java.time.LocalDateTime

case class Batch(number: Int,
                 initiated: LocalDateTime,
                 completed: LocalDateTime,
                 recipe: Recipe)