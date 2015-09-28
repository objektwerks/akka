package app

sealed trait Step {
  def ingrediants: List[Ingrediant]
  def brew(): Unit
}
case class Mashing(ingrediants: List[Ingrediant]) extends Step {
  def brew(): Unit = {}
}
case class Boiling(ingrediants: List[Ingrediant]) extends Step {
  def brew(): Unit = {}
}
case class Cooling(ingrediants: List[Ingrediant]) extends Step {
  def brew(): Unit = {}
}
case class Fermenting(ingrediants: List[Ingrediant]) extends Step {
  def brew(): Unit = {}
}
case class Conditioning(ingrediants: List[Ingrediant]) extends Step {
  def brew(): Unit = {}
}
case class Packaging(ingrediants: List[Ingrediant]) extends Step {
  def brew(): Unit = {}
}