package app

import java.time.LocalDateTime

case class Recipe(steps: List[Step]) {
  def brew(): LocalDateTime = {
    steps foreach { s => s.execute() }
    LocalDateTime.now()
  }
}