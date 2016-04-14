package words

import scala.io.Source

object Words {
  val list = Source.fromInputStream(getClass.getResourceAsStream("/license.mit")).mkString.split("\\P{L}+").toList
  val words = list.sliding(8).toList
}