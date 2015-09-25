package akka

sealed trait Ingrediant
case object Hops extends Ingrediant

case class Recipe(ingrediants: Map[String, Ingrediant], steps: Map[String, Step]) {

}