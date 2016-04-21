package words

import scala.concurrent.duration.FiniteDuration

class Collector[T](val timeout: FiniteDuration, val collect: Int, private var collection: IndexedSeq[T]) {
  def add(item: T): this.type = {
    collection = item +: collection
    this
  }

  def sequence: IndexedSeq[T] = collection

  def count: Int = collection.size

  def isDone: Boolean = collect == collection.size
}