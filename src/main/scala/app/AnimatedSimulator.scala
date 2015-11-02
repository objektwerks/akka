package app

import command.Command
import domain.IPA
import event.Event
import system.Brewery

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.beans.property.ObjectProperty
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ToolBar}
import scalafx.scene.layout.VBox

// TODO
class AnimatedSimulator extends JFXApp {
  val commandProperty = new ObjectProperty[Command]()
  val eventProperty = new ObjectProperty[Event]()
  Brewery.register(commandProperty, eventProperty)

  val brewButton = new Button {
    text = "Brew"
  }

  val toolbar = new ToolBar {
    content = List(brewButton)
  }

  val contentPane = new VBox {
    spacing = 6
    padding = Insets(6)
    children = List()
  }

  val appPane = new VBox {
    prefWidth = 660
    spacing = 6
    padding = Insets(6)
    children = List(toolbar, contentPane)
  }

  stage = new JFXApp.PrimaryStage {
    title = "Brew Meister"
    scene = new Scene {
      root = appPane
    }
    onCloseRequest = handle {
      Brewery.shutdown()
    }
  }

  brewButton.onAction = { ae: ActionEvent =>
    val recipe = IPA()
    brewButton.disable = true
    Brewery.brew(recipe)
  }

  commandProperty.onChange { (_, _, newCommand) =>
  }

  eventProperty.onChange { (_, _, newEvent) =>
  }
}