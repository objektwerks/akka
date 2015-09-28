package app

import akka.actor.{ActorRef, ActorSystem, Props}

import scalafx.application.JFXApp
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.ToolBar
import scalafx.scene.layout.VBox

object App extends JFXApp {
  val system: ActorSystem = ActorSystem.create("IPAFactory")
  val brewmeister: ActorRef = system.actorOf(Props[BrewMeister], name = "brewmeister")

  val toolbar = new ToolBar {
    content = List()
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
    title.value = "IPA Factory"
    scene = new Scene {
      root = appPane
    }
  }
}