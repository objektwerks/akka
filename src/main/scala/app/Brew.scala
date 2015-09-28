package app

object Brew {
  case class Recipe(name: String, description: String)
  case class Mash(recipe: Recipe, malt: Ingredient.Malt)
  case class Boil(recipe: Recipe, hops: Ingredient.Hops)
  case class Cool(recipe: Recipe)
  case class Ferment(recipe: Recipe, yeast: Ingredient.Yeast)
  case class Condition(recipe: Recipe)
  case class Bottle(recipe: Recipe)
}