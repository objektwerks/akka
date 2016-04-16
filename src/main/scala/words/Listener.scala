package words

import akka.actor.{Actor, ActorLogging, Props, Terminated}

class Listener extends Actor with ActorLogging {
  val client = context.parent

  override def receive: Receive = {
    case request: Request =>
      val master = context.actorOf(Props[Master], name = Master.newMasterName)
      context.watch(master)
      master ! ListOfCountWords(request.words map { words => CountWords(request.uuid, words) })
    case wordsCounted: WordsCounted =>
      client ! Response(wordsCounted)
      context.stop(sender)
    case fault: Fault =>
      client ! fault
      context.stop(sender)
    case Terminated(master) =>
      client ! Fault(s"Master [${master.path.name}] has been terminated!")
      context.stop(sender)
  }
}