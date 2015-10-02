package client

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, ActorPath, ActorSystem}
import akka.cluster.client.{ClusterClient, ClusterClientSettings}
import com.typesafe.config.ConfigFactory
import domain.{IPA, Recipe}
import event.Brewed

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ToolBar}
import scalafx.scene.layout.VBox

object BreweryProxy {
  val system = ActorSystem.create("Brewery", ConfigFactory.load("app.conf"))
  val seeds = Set(ActorPath.fromString("akka.tcp://Brewery@host1:2551/system/receptionist"),
                  ActorPath.fromString("akka.tcp://Brewery@host2:2552/system/receptionist"))
  val settings = ClusterClientSettings(system).withInitialContacts(seeds)
  val proxy: ActorRef = system.actorOf(ClusterClient.props(settings), name = "proxy")

  def brew(recipe: Recipe): Unit = {
    proxy ! ClusterClient.Publish("brew", IPA())
  }
}

object App extends JFXApp {
  val brewButton = new Button {
    text = "Brew"
    onAction = { ae: ActionEvent => println("Clicked!") }
  }

  val toolbar = new ToolBar {
    content = List(brewButton)
  }

  val contentPane = new VBox {
    maxWidth = 400
    maxHeight = 400
    spacing = 6
    padding = Insets(6)
    children = List()
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
}