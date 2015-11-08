package domain

final case class Ingrediant(kind: String, amount: Double, measurement: Measurement.Value)
final case class Malt(kind: String, amount: Double, measurement: Measurement.Value)
final case class Hop(kind: String, oz: Double)
final case class Yeast(kind: String, oz: Double)
final case class Water(gallons: Double, boilSizeInGallons: Double, boildTimeInMinutes: Int, batchSizeInGallons: Double)
final case class Fermentation(days: Int, degrees: Int)
object Phase extends Enumeration {
  type Phase = Value
  val Masher, Boiler, Cooler, Fermenter, Conditioner = Value
}
object Measurement extends Enumeration {
  type Measurement = Value
  val lb, oz, tsp = Value
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
  def primary: Fermentation
  def secondary: Fermentation
  def instructions: Map[Phase.Value, List[String]]
}

import Phase._
import Measurement._

final case class IPA(name: String = "Dogfish Head 60' IPA",
                     style: String = "IPA",
                     ibu: Int = 60,
                     abv: Double = 5.8,
                     originalGravity: Double = 1.070,
                     finalGravity: Double = 1.018,
                     color: Double = 4.8,
                     ingrediants: List[Ingrediant] = List(Ingrediant("Irish Moss", 1.0, tsp),
                                                          Ingrediant("Corn Sugar", 4.0, oz)),
                     malts: List[Malt] = List(Malt("2 Row Pale", 13.0, lb),
                                              Malt("Thomas Fawcett Amber Malt", 6.0, oz)),
                     hops: List[Hop] = List(Hop("Simcoe", 0.5),
                                            Hop("Amarillo", 1.0),
                                            Hop("Palisade", 0.5),
                                            Hop("Glacier", 0.5),
                                            Hop("Warrior", 0.75)),
                     yeast: Yeast = Yeast("English Ale", 1.0),
                     water: Water = Water(5.0, 6.0, 60, 5.0),
                     primary: Fermentation = Fermentation(10, 63),
                     secondary: Fermentation = Fermentation(10, 63),
                     instructions: Map[Phase.Value, List[String]] = Map(Masher -> List("Mash."),
                                                                    Boiler -> List("Boil."),
                                                                    Cooler -> List("Cool."),
                                                                    Fermenter -> List("Ferment."),
                                                                    Conditioner -> List("Condition."))) extends Recipe