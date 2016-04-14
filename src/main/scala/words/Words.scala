package words

import scala.io.Source

object Words {
  val list = Source.fromInputStream(getClass.getResourceAsStream("/license.mit")).mkString.split("\\P{L}+").toList
  val words = list.grouped(list.length / 8).toList // 168 / 8 = 21
}