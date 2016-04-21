package words

import scala.concurrent.duration.FiniteDuration

final case class CollectorEvent[T](part: Int, of: Int, data: T)

class Collector[T](val timeout: FiniteDuration, val collect: Int, private var collection: IndexedSeq[T]) {
  def add(item: T): this.type = {
    collection = item +: collection
    this
  }

  def event: CollectorEvent[T] = CollectorEvent[T](collection.size, collect, collection.last)

  def sequence: IndexedSeq[T] = collection

  def count: Int = collection.size

  def isDone: Boolean = collect == collection.size
}