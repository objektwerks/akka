package domain

case class Ingrediant(kind: String, amount: String)
case class Malt(kind: String, amount: String)
case class Hop(kind: String, amount: String)
case class Yeast(kind: String, amount: String)
case class Water(kind: String, amount: String, boilSize: String, boilTime: String, batchSize: String)
case class Fermentation(kind: String, days: Int, degrees: Int)
case class Step(step: String)

sealed trait Recipe {
  def name: String
  def description: String
  def ibu: Int
  def abv: Double
  def originalGravity: Double
  def finalGravity: Double
  def color: Double
  def ingrediants: List[Ingrediant]
  def malts: List[Malt]
  def hops: List[Hop]
  def yeast: Yeast
  def water: Water
  def fermentations: List[Fermentation]
  def steps: List[Step]
}

case class IPA(name: String = "IPA",
               description: String = "Dogfish Head 60' IPA",
               ibu: Int = 60,
               abv: Double = 5.8,
               originalGravity: Double = 1.070,
               finalGravity: Double = 1.018,
               color: Double = 4.8,
               ingrediants: List[Ingrediant] = List(Ingrediant("Irish Moss", "1 tsp"), Ingrediant("Corn Sugar", "4 oz")),
               malts: List[Malt] = List(Malt("2 Row Pale", "13 lb"), Malt("Thomas Fawcett Amber Malt", "6 oz")),
               hops: List[Hop] = List(Hop("Simcoe", ".5 oz"), Hop("Amarillo", "1.0 oz"), Hop("Palisade", ".5 oz"), Hop("Glacier", ".5 oz"), Hop("Warrior", ".75 oz")),
               yeast: Yeast = Yeast("English Ale", "1 oz"),
               water: Water = Water("Spring", "5 g", "6 g", "60 m", "5 g"),
               fermentations: List[Fermentation] = List(Fermentation("primary", 10, 63), Fermentation("secondary", 10, 63)),
               steps: List[Step] = List(Step("Mash at 152F for 60 m."),
                                        Step("Add hops and Irish Moss. Cool and pitch yeast."),
                                        Step("Start dry hopping in secondary fermentor."),
                                        Step("Hold in primary fermentor for 10 days at 66F."),
                                        Step("Transfer to secondary fermentor for 10 days."),
                                        Step("Carbonate and age for 14 days."))) extends Recipe