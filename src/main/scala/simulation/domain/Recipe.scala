package simulation.domain

final case class Gravity(original: Double, specific: Double, finished: Double)
final case class Adjunct(kind: String, amount: Double, as: Measurement.Value)
final case class Malt(kind: String, amount: Double, as: Measurement.Value)
final case class Hop(kind: String, amount: Double, as: Measurement.Value)
final case class Yeast(kind: String, amount: Double, as: Measurement.Value)
final case class Water(gallons: Double, boilSizeInGallons: Double, boilTimeInMinutes: Int, batchSizeInGallons: Double)
final case class Fermentation(days: Int, degrees: Int)
object Measurement extends Enumeration { val lb, oz, tsp = Value }

sealed trait Recipe {
  def name: String
  def style: String
  def ibu: Int
  def color: Double
  def abv: Double
  def gravity: Gravity
  def adjuncts: List[Adjunct]
  def malts: List[Malt]
  def hops: List[Hop]
  def yeast: Yeast
  def water: Water
  def primary: Fermentation
  def secondary: Fermentation
  def mash: String
  def boil: String
  def cool: String
  def ferment: String
  def condition: String
  def bottle: String
  def keg: String
  def cask: String
}

import simulation.domain.Measurement._

final case class IPA(name: String = "Dogfish Head 60' IPA",
                     style: String = "IPA",
                     ibu: Int = 60,
                     color: Double = 4.8,
                     abv: Double = 5.8,
                     gravity: Gravity = Gravity(original = 1.070, specific = 1.00, finished = 1.018),
                     adjuncts: List[Adjunct] = List(Adjunct("Irish Moss", 1.0, tsp),
                                                    Adjunct("Corn Sugar", 4.0, oz)),
                     malts: List[Malt] = List(Malt("2 Row Pale", 13.0, lb),
                                              Malt("Thomas Fawcett Amber Malt", 6.0, oz)),
                     hops: List[Hop] = List(Hop("Simcoe", 0.5, oz),
                                            Hop("Amarillo", 1.0, oz),
                                            Hop("Palisade", 0.5, oz),
                                            Hop("Glacier", 0.5, oz),
                                            Hop("Warrior", 0.75, oz)),
                     yeast: Yeast = Yeast("English Ale", 1.0, oz),
                     water: Water = Water(gallons = 5.0, boilSizeInGallons = 6.0, boilTimeInMinutes = 60, batchSizeInGallons = 5.0),
                     primary: Fermentation = Fermentation(days = 10, degrees = 63),
                     secondary: Fermentation = Fermentation(days = 10, degrees = 63),
                     mash: String = "Add malt to water at 150 degrees for 60 minutes.",
                     boil: String = "Add hops to water and boil 60 minutes.",
                     cool: String = "All wort to cool.",
                     ferment: String = "Add yeast and ferment for 10 days.",
                     condition: String = "Add adjuncts and ferment for 10 days.",
                     bottle: String = "Bottle 48-54 12 oz bottles of beer.",
                     keg: String = "Keg beer.",
                     cask: String = "Cask beer.") extends Recipe