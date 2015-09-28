package app

trait Step {
  def name: String
  def ingrediants: List[Ingrediant]
  def brew(): Unit
}