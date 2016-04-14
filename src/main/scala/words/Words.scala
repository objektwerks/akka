package words

import scala.io.Source

object Words {
  val list = Source.fromInputStream(getClass.getResourceAsStream("/license.mit")).mkString.split("\\P{L}+").toList
  val words = list.grouped(21).toList // list.length = 168 / 21 = 8
}