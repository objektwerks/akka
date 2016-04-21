package words

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration

class Collector[T](val timeout: FiniteDuration, val collect: Int, private val collection: mutable.Buffer[T]) {
  def add(item: T): this.type = {
    collection += item
    this
  }

  def list: List[T] = collection.toList

  def count: Int = collection.size

  def isDone: Boolean = collect == collection.size
}