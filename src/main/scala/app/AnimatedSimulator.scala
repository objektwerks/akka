package app

import command.{Brew, Command}
import domain.IPA
import event.{Brewed, Event}
import system.Brewery

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.beans.property.ObjectProperty
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ToolBar}
import scalafx.scene.layout.VBox

object AnimatedSimulator extends JFXApp {
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
    brewButton.disable = true
    Brewery.brew(new IPA())
  }

  commandProperty.onChange { (_, _, newCommand) =>
    newCommand match {
      case Brew(batch, recipe) =>
    }
  }

  eventProperty.onChange { (_, _, newEvent) =>
    newEvent match {
      case Brewed(batch) => brewButton.disable = false
      case _ =>
    }
  }
}