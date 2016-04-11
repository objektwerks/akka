package words

import scala.io.Source

object Words {
  val array = Source.fromInputStream(getClass.getResourceAsStream("/license.mit")).mkString.split("\\P{L}+")
  val (left, right) = array.splitAt(array.size / 2)
}