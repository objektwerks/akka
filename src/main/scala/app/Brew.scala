package app

object Brew {
  case class Recipe(name: String)
  case class Mash(malt: String)
  case class Boil(hops: String)
  case class Cool()
  case class Ferment(yeast: String)
  case class Condition()
  case class Bottle()
}