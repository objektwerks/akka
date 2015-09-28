package app

object Ingredient {
  case class Malt(kind: String)
  case class Hop(kind: String)
  case class Hops(kinds: Array[Hop])
  case class Yeast(kind: String)
  case class Water(kind: String)
}