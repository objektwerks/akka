package client

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.cluster.pubsub.DistributedPubSubMediator.SubscribeAck
import akka.cluster.pubsub.{DistributedPubSub, DistributedPubSubMediator}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import domain.Recipe
import event.Brewed
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}
import scalafx.beans.property.ObjectProperty

class BreweryPublisher extends Actor with ActorLogging {
  import DistributedPubSubMediator.Publish
  val mediator = DistributedPubSub(context.system).mediator

  override def receive: Receive = {
    case recipe: Recipe =>
      log.info(s"Brewery Publisher recipe: $recipe")
      mediator ! Publish(topic = "recipe", recipe)
  }
}

class BrewerySubscriber extends Actor with ActorLogging {
  import DistributedPubSubMediator.Subscribe
  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(topic = "brewed", self)

  override def receive: Receive = {
    case brewed: Brewed =>
      log.info(s"Brewery Subscriber brewed: $brewed")
      BreweryProxy.brewed(brewed)
    case SubscribeAck(Subscribe("brewed", None, `self`)) => log.info("Brewery Subscriber subscribed to brewed topic.")
  }
}

object BreweryProxy {
  implicit val ec = ExecutionContext.global
  implicit val timeout = new Timeout(3, TimeUnit.SECONDS)
  val log = LoggerFactory.getLogger(this.getClass)
  val system = ActorSystem.create("Brewery", ConfigFactory.load("app.conf"))
  val publisher: ActorRef = system.actorOf(Props[BreweryPublisher], name = "brewery.client.publisher")
  val subscriber: ActorRef = system.actorOf(Props[BrewerySubscriber], name = "brewery.client.subscriber")
  var brewedPropertyListener: Option[ObjectProperty[Brewed]] = None
  log.info("Brewery Proxy initialized!")

  def register(brewedProperty: ObjectProperty[Brewed]): Unit = {
    brewedPropertyListener = Some(brewedProperty)
  }

  def brew(recipe: Recipe): Unit = {
    Future { publisher ! recipe }
  }

  def brewed(brewed: Brewed): Unit = {
    brewedPropertyListener foreach { _.value = brewed }
  }
}