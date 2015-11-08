package domain

case class Ingrediant(kind: String, amount: String)
case class Malt(kind: String, amount: String)
case class Hop(kind: String, amount: String)
case class Yeast(kind: String, amount: String)
case class Water(kind: String, amount: String, boilSize: String, boilTime: String, batchSize: String)
case class Fermentation(kind: String, days: Int, degrees: Int)
case class Step(step: String)
object Phase extends Enumeration {
  type Phase = Value
  val Masher, Boiler, Cooler, Fermenter, Conditioner = Value
}

sealed trait Recipe {
  def name: String
  def style: String
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
  def stepsByPhase: Map[Phase.Value, List[Step]]
}

case class IPA(name: String = "Dogfish Head 60' IPA",
               style: String = "IPA",
               ibu: Int = 60,
               abv: Double = 5.8,
               originalGravity: Double = 1.070,
               finalGravity: Double = 1.018,
               color: Double = 4.8,
               ingrediants: List[Ingrediant] = List(Ingrediant("Irish Moss", "1 tsp"), Ingrediant("Corn Sugar", "4 oz")),
               malts: List[Malt] = List(Malt("2 Row Pale", "13 lb"), Malt("Thomas Fawcett Amber Malt", "6 oz")),
               hops: List[Hop] = List(Hop("Simcoe", ".5 oz"),
                                      Hop("Amarillo", "1.0 oz"),
                                      Hop("Palisade", ".5 oz"),
                                      Hop("Glacier", ".5 oz"),
                                      Hop("Warrior", ".75 oz")),
               yeast: Yeast = Yeast("English Ale", "1 oz"),
               water: Water = Water("Spring", "5 g", "6 g", "60 m", "5 g"),
               fermentations: List[Fermentation] = List(Fermentation("primary", 10, 63), Fermentation("secondary", 10, 63)),
               stepsByPhase: Map[Phase.Value, List[Step]] = Map(Phase.Masher -> List(Step("Do A.")),
                                                                Phase.Boiler -> List(Step("Do B.")),
                                                                Phase.Cooler -> List(Step("Do C.")),
                                                                Phase.Fermenter -> List(Step("Do D.")),
                                                                Phase.Conditioner -> List(Step("Do E.")))) extends Recipe