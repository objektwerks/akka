package domain

case class Ingrediant(kind: String, amount: String)
case class Malt(kind: String, amount: String)
case class Hop(kind: String, amount: String)
case class Yeast(kind: String, amount: String)
case class Water(kind: String, amount: String)
case class Step(step: String)

sealed trait Recipe {
  def name: String
  def description: String
  def ibu: Int
  def abv: Double
  def ingrediants: List[Ingrediant]
  def malts: List[Malt]
  def hops: List[Hop]
  def yeast: Yeast
  def water: Water
  def steps: List[Step]
}

case class IPA(name: String = "IPA",
               description: String = "Indian Pale Ale",
               ibu: Int = 60,
               abv: Double = 5.8,
               ingrediants: List[Ingrediant] = List(),
               malts: List[Malt] = List(Malt("2 Row Pale", "13lb"), Malt("Thomas Fawcett Amber Malt", "6oz")),
               hops: List[Hop] = List(Hop("Simcoe", ".5oz"), Hop("Amarillo", ".5oz"), Hop("Palisade", ".5oz"), Hop("Glacier", ".5oz"), Hop("Warrior", ".5oz")),
               yeast: Yeast = Yeast("English Ale", "1oz"),
               water: Water = Water("Spring", "5g"),
               steps: List[Step] = List()) extends Recipe