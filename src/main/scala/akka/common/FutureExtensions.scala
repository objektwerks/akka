package common

import akka.actor.ActorSystem
import akka.pattern.after

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object FutureExtensions {
  implicit class Extensions[T](future: Future[T]) {
    def withTimeout(timeoutException: => Throwable)
                   (implicit system: ActorSystem,
                    ec: ExecutionContext,
                    timeout: FiniteDuration): Future[T] = {
      Future.firstCompletedOf(
        Seq(
          future,
          after(timeout, system.scheduler)(Future.failed(timeoutException))
        )
      )
    }
  }
}