package app

trait Recipe {
  def name: String
  def description: String
  def malt: Malt
  def hops: Hops
  def yeast: Yeast
  def water: Water
}

case class Malt(kind: String)

case class Hop(kind: String)

case class Hops(kinds: Array[Hop])

case class Yeast(kind: String)

case class Water(kind: String)