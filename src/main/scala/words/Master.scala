package words

import akka.actor.{Actor, ActorLogging, ActorRef, Props, SupervisorStrategy, Terminated}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool

import scala.collection.mutable
import scala.util.Random

class Master extends Actor with ActorLogging {
  val listener = context.parent
  val workers = mutable.ArrayBuffer.empty[ActorRef]
  val random = new Random
  val router = context.actorOf(ClusterRouterPool(BroadcastPool(2), ClusterRouterPoolSettings(totalInstances = 2,
    maxInstancesPerNode = 1,
    allowLocalRoutees = false,
    useRole = Some("worker"))).props(Props[Worker]), name = "worker-router")

  override def supervisorStrategy: SupervisorStrategy = SupervisorStrategy.stoppingStrategy

  override def receive: Receive = {
    case countWords: CountWords if workers.isEmpty => sender ! WorkerUnavailable(countWords)
    case countWords: CountWords => workers(random.nextInt(workers.length)) ! countWords
    case wordsCounted: WordsCounted => listener ! wordsCounted
    case RegisterWorker if !workers.contains(sender) =>
      context watch sender
      workers += sender
    case Terminated(worker) => workers -= worker
  }
}