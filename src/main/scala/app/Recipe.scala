package app

case class Recipe(steps: List[Step]) {
  def brew(): Unit = steps foreach { s => s.initiate() }
}