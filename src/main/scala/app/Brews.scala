package app

import java.time.LocalDateTime

case class Malt(kind: String)

case class Hop(kind: String)

case class Yeast(kind: String)

case class Water(kind: String)

trait Recipe {
  def name: String
  def description: String
  def malt: Malt
  def hops: List[Hop]
  def yeast: Yeast
  def water: Water
}

case class IPA(name: String = "IPA",
               description: String = "Indian Pale Ale",
               malt: Malt = Malt("Caramel"),
               hops: List[Hop] = List(Hop("Cascade"), Hop("Amarillo")),
               yeast: Yeast = Yeast("Brewers"),
               water: Water = Water("Spring")) extends Recipe

case class Batch(number: Int,
                 initiated: LocalDateTime,
                 completed: LocalDateTime,
                 recipe: Recipe)