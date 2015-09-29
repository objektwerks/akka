package app

trait Recipe {
  def name: String
  def description: String
  def malt: Malt
  def hops: Hops
  def yeast: Yeast
  def water: Water
}
