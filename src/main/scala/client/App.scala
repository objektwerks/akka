package client

import akka.actor.{ActorPath, ActorRef, ActorSystem}
import akka.cluster.client.{ClusterClient, ClusterClientSettings}
import com.typesafe.config.ConfigFactory
import domain.{IPA, Recipe}

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ToolBar}
import scalafx.scene.layout.VBox

object BreweryClient {
  val system = ActorSystem.create("Brewery", ConfigFactory.load("app.conf"))
  val seeds = Set(ActorPath.fromString("akka.tcp://Brewery@host1:2551/system/receptionist"),
                  ActorPath.fromString("akka.tcp://Brewery@host2:2552/system/receptionist"))
  val settings = ClusterClientSettings(system).withInitialContacts(seeds)
  val client: ActorRef = system.actorOf(ClusterClient.props(settings), name = "proxy")

  def brew(recipe: Recipe): Unit = {
    client ! ClusterClient.Publish("brew", IPA())
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