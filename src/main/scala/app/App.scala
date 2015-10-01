package app

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ToolBar}
import scalafx.scene.layout.VBox

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