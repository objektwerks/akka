package domain

case class Malt(kind: String, amount: String)
case class Hop(kind: String, amount: String)
case class Yeast(kind: String, amount: String)
case class Water(kind: String, amount: String)

sealed trait Recipe {
  def name: String
  def description: String
  def ibu: Int
  def abv: Double
  def malt: Malt
  def hops: List[Hop]
  def yeast: Yeast
  def water: Water
}

case class IPA(name: String = "IPA",
               description: String = "Indian Pale Ale",
               ibu: Int = 60,
               abv: Double = 5.8,
               malt: Malt = Malt("Pale", "12lb"),
               hops: List[Hop] = List(Hop("Simcoe", ".5oz"), Hop("Amarillo", ".5oz"), Hop("Palisade", ".5oz"), Hop("Glacier", ".5oz")),
               yeast: Yeast = Yeast("English Ale", "1oz"),
               water: Water = Water("Spring", "5g")) extends Recipe

case class DIPA(name: String = "DIPA",
                description: String = "Double Indian Pale Ale",
                ibu: Int = 75,
                abv: Double = 9.0,
                malt: Malt = Malt("Pale", "12lb"),
                hops: List[Hop] = List(Hop("Simcoe", ".5oz"), Hop("Amarillo", ".5oz"), Hop("Palisade", ".5oz"), Hop("Glacier", ".5oz")),
                yeast: Yeast = Yeast("English Ale", "1oz"),
                water: Water = Water("Spring", "5g")) extends Recipe