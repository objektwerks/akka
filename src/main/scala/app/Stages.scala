package app

case class Recipe(name: String, description: String)

case class Mash(recipe: Recipe, malt: Malt)

case class Boil(recipe: Recipe, hops: Hops)

case class Cool(recipe: Recipe)

case class Ferment(recipe: Recipe, yeast: Yeast)

case class Condition(recipe: Recipe)

case class Bottle(recipe: Recipe)