package app

import java.time.LocalDateTime

case class Recipe(name: String, steps: List[Step]) {
  def brew: LocalDateTime = {
    steps foreach { step => step.brew() }
    LocalDateTime.now()
  }
}