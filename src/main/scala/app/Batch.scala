package app

import java.time.LocalDateTime

case class Batch(recipe: Recipe,
                 initiated: LocalDateTime = LocalDateTime.now(),
                 completed: LocalDateTime = LocalDateTime.now()) {
  def brew(): Unit = {
    recipe.brew()
    completed.adjustInto(LocalDateTime.now())
  }
}