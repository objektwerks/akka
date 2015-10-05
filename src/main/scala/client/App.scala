package client

import akka.actor._
import akka.cluster.pubsub.DistributedPubSubMediator.SubscribeAck
import akka.cluster.pubsub.{DistributedPubSub, DistributedPubSubMediator}
import com.typesafe.config.ConfigFactory
import domain.{IPA, Recipe}
import event.Brewed

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.beans.property.ObjectProperty
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{Separator, Label, Button, ToolBar}
import scalafx.scene.layout.VBox
import scalafx.scene.text.Text

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
  var recipePropertyListener: ObjectProperty[Recipe] = _
  var brewedPropertyListener: ObjectProperty[Brewed] = _

  def register(recipeProperty: ObjectProperty[Recipe], brewedProperty: ObjectProperty[Brewed]): Unit = {
    recipePropertyListener = recipeProperty
    brewedPropertyListener = brewedProperty
  }

  def brew(recipe: Recipe): Unit = {
    publisher ! recipe
    recipePropertyListener.value = recipe
  }

  def brewed(brewed: Brewed): Unit = {
    brewedPropertyListener.value = brewed
  }
}

object App extends JFXApp {
  val brewButton = new Button {
    text = "Brew"
    onAction = { ae: ActionEvent => BreweryClient.brew(IPA()) }
  }

  val statusBar = new Label

  val toolbar = new ToolBar {
    content = List(brewButton, new Separator(), statusBar)
  }

  val statusText = new Text

  val contentPane = new VBox {
    maxWidth = 400
    maxHeight = 400
    spacing = 6
    padding = Insets(6)
    children = List(statusText)
  }

  val appPane = new VBox {
    maxWidth = 400
    maxHeight = 400
    spacing = 6
    padding = Insets(6)
    children = List(toolbar, contentPane)
  }

  stage = new JFXApp.PrimaryStage {
    title = "IPA Brewery"
    scene = new Scene {
      root = appPane
    }
  }

  val recipeProperty = new ObjectProperty[Recipe]()
  recipeProperty.onChange({
    statusBar.text = "Brewing..."
    statusText.text = recipeProperty.value.toString
  })

  val brewedProperty = new ObjectProperty[Brewed]()
  brewedProperty.onChange({
    statusBar.text = "Brewed!"
    statusText.text = brewedProperty.value.toString
  })

  BreweryClient.register(recipeProperty, brewedProperty)
}