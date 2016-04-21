package words

import scala.concurrent.duration.FiniteDuration

class Collector[T](val timeout: FiniteDuration, val collect: Int, private var collection: List[T]) {
  def add(item: T): this.type = {
    collection = item +: collection
    this
  }

  def list: List[T] = collection

  def count: Int = collection.size

  def isDone: Boolean = collect == collection.size
}