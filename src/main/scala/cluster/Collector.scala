package cluster

import java.util.concurrent.atomic.AtomicInteger

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration

final case class CollectorEvent[T](part: Int, of: Int, data: T)

class Collector[T](val timeout: FiniteDuration, val collect: Int, private val collection: mutable.ArrayBuffer[T]) {
  val countDown = new AtomicInteger(collect)

  def add(item: T): this.type = {
    collection += item
    countDown.decrementAndGet
    this
  }

  def event: CollectorEvent[T] = CollectorEvent[T](collect - countDown.get, collect, collection.last)

  def sequence: IndexedSeq[T] = collection

  def count: Int = collect - countDown.get

  def isDone: Boolean = countDown.get == 0
}