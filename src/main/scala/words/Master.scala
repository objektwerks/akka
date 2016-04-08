package words

import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}

class Master extends Actor with ActorLogging {
  var workers = IndexedSeq.empty[ActorRef]
  var counter = 0

  def receive = {
    case countWords: CountWords if workers.isEmpty => log.error("No workers available!")
    case countWords: CountWords =>
      counter += 1
      workers(counter % workers.size) forward countWords
    case wordsCounted: WordsCounted => context.system.log.info(wordsCounted.toString)
    case WorkerRegistration if !workers.contains(sender()) =>
      context watch sender
      workers = workers :+ sender
    case Terminated(worker) => workers = workers.filterNot(_ == worker)
  }
}