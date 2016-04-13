package words

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.util.Timeout

import scala.concurrent.duration._

class Client extends Actor with ActorLogging {
  val listener = context.actorSelection("/user/listener")

  Cluster(context.system).registerOnMemberUp {
    implicit val ec = context.dispatcher
    implicit val timeout = Timeout(3 seconds)
    context.system.scheduler.schedule(3 seconds, 3 seconds) {
      self ! Request()
    }
  }

  override def receive: Receive = {
    case request: Request => listener ! Request
    case response: Response => log.info(s"Response received: $response")
  }
}