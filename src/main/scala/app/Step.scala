package app

import java.time.LocalDateTime

trait Step {
  def name: String
  def initiated: LocalDateTime
  def completed: LocalDateTime
  def ingrediants: List[Ingrediant]
  def initiate(): Unit
}