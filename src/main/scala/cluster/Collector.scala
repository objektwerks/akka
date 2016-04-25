package cluster

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration

final case class CollectorEvent[T](part: Int, of: Int, data: T)

class Collector[T](val timeout: FiniteDuration, val collect: Int, private var collection: mutable.ArrayBuffer[T]) {
  var countDown = collect

  def add(item: T): this.type = {
    collection += item
    countDown = countDown - 1
    this
  }

  def event: CollectorEvent[T] = CollectorEvent[T](collect - countDown, collect, collection.last)

  def sequence: IndexedSeq[T] = collection

  def count: Int = collect - countDown

  def isDone: Boolean = countDown == 0
}