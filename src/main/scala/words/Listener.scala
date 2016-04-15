package words

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, Props, Terminated}

object Listener {
  private val masterNumber = new AtomicInteger()
  def nextMasterNumber: Int = masterNumber.incrementAndGet()
}

class Listener extends Actor with ActorLogging {
  val publisher = context.system.eventStream

  override def receive: Receive = {
    case request: Request =>
      log.info(s"Listener received request from Client.")
      val master = context.actorOf(Props[Master], name = s"master-${Listener.nextMasterNumber}")
      log.info(s"Listener created Master [${master.path.name}].")
      context.watch(master)
      val listOfCountWords = ListOfCountWords(request.words map { words => CountWords(request.uuid, words) })
      master ! listOfCountWords
    case wordsCounted: WordsCounted =>
      log.info("Listener received words counted, and sent response to Client.")
      publisher.publish(Response(wordsCounted))
      context.stop(sender)
    case fault: Fault =>
      log.error("Listener received fault, sent to Client and stopped Master!")
      publisher.publish(fault)
      context.stop(sender)
    case Terminated(master) =>
      log.error("Listener received Master Terminated message, sent fault to Client, terminated Master!")
      publisher.publish(Fault(s"Master [${master.path.name}] has been terminated"))
      context.stop(sender)
  }
}