package simulation

import scala.annotation.tailrec

object Simulator {
  private[this] def tailrec(n: Long): BigInt = {
    @tailrec
    def loop(n: Long, a: Long, b: Long): BigInt = n match {
      case 0 => a
      case _ => loop(n - 1, b, a + b)
    }
    loop(n, 0, 1)
  }

  def simulate(number: Int): BigInt = {
    tailrec(number)
  }
}