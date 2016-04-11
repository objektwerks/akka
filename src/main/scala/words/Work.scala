package words

import java.time.LocalTime
import java.util.UUID

case class Id(uuid: String = UUID.randomUUID.toString)

sealed trait State
case object ReadyForWork extends State

sealed trait Command {
  def id: Id = Id()
  def assigned: LocalTime = LocalTime.now
}
final case class CountWords(words: Array[String]) extends Command

sealed trait Event {
  def commandId: Id
  def completed: LocalTime = LocalTime.now
}
final case class WordsCounted(commandId: Id, counts: Map[String, Int]) extends Event