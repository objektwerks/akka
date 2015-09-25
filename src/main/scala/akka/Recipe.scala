package akka

sealed trait Ingrediant

case class Recipe(ingrediants: Map[String, Ingrediant]) {

}