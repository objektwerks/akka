package app

sealed trait Ingrediant {
  def kind: String
  def amount: Float
  def unit: String
}
case class Malt(kind: String, amount: Float, unit: String) extends Ingrediant
case class Hop(kind: String, amount: Float, unit: String) extends Ingrediant
case class Yeast(kind: String, amount: Float, unit: String) extends Ingrediant
case class Water(kind: String, amount: Float, unit: String) extends Ingrediant