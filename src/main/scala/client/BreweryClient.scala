package client

import akka.actor._
import akka.cluster.pubsub.DistributedPubSubMediator.SubscribeAck
import akka.cluster.pubsub.{DistributedPubSub, DistributedPubSubMediator}
import com.typesafe.config.ConfigFactory
import domain.Recipe
import event.Brewed

import scalafx.beans.property.ObjectProperty

class BreweryClientPublisher extends Actor {
  import DistributedPubSubMediator.Publish
  val mediator = DistributedPubSub(context.system).mediator

  override def receive: Receive = {
    case recipe: Recipe => mediator ! Publish(topic = "recipe", recipe)
  }
}

class BreweryClientSubscriber extends Actor with ActorLogging {
  import DistributedPubSubMediator.Subscribe
  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(topic = "brewed", self)

  override def receive: Receive = {
    case brewed: Brewed => BreweryClient.brewed(brewed)
    case SubscribeAck(Subscribe("brewed", None, `self`)) => log.info("Brewery Client Subscriber subscribing to brewed topic.")
  }
}

object BreweryClient {
  val system = ActorSystem.create("Brewery", ConfigFactory.load("app.conf"))
  val publisher: ActorRef = system.actorOf(Props[BreweryClientPublisher], name = "brewery.client.publisher")
  val subscriber: ActorRef = system.actorOf(Props[BreweryClientSubscriber], name = "brewery.client.subscriber")
  var recipePropertyListener: Option[ObjectProperty[Recipe]] = None
  var brewedPropertyListener: Option[ObjectProperty[Brewed]] = None

  def register(recipeProperty: ObjectProperty[Recipe], brewedProperty: ObjectProperty[Brewed]): Unit = {
    recipePropertyListener = Some(recipeProperty)
    brewedPropertyListener = Some(brewedProperty)
  }

  def brew(recipe: Recipe): Unit = {
    recipePropertyListener foreach { _.value = recipe }
    publisher ! recipe
  }

  def brewed(brewed: Brewed): Unit = {
    brewedPropertyListener foreach { _.value = brewed }
  }
}