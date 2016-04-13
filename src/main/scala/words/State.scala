package words

sealed trait State

final case class WorkerUnavailable(countWords: CountWords) extends State