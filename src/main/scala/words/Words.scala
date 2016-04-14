package words

import scala.io.Source

object Words {
  val words = Source
    .fromInputStream(getClass.getResourceAsStream("/license.mit"))
    .mkString
    .split("\\P{L}+")
    .toList
    .sliding(8)
    .toList
}