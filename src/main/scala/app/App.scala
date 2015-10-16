package app

import command.{Brew, Command}
import domain.IPA
import event.{Brewed, Event}
import system.Brewery

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.beans.property.ObjectProperty
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.VBox
import scalafx.scene.text.Text

object App extends JFXApp {
  val commandProperty = new ObjectProperty[Command]()
  val eventProperty = new ObjectProperty[Event]()
  Brewery.register(commandProperty, eventProperty)

  val brewButton = new Button {
    text = "Brew"
  }

  val toolbar = new ToolBar {
    content = List(brewButton)
  }

  val recipeLabel = new Label {
    text = "Receipe"
  }

  val recipeText = new Text {
    wrappingWidth = 600
  }

  val commandLabel = new Label {
    text = "Commands"
  }

  val commandText = new Text {
    wrappingWidth = 600
  }

  val eventLabel = new Label {
    text = "Events"
  }

  val eventList = new ListView[String] {
    prefHeight = 475
    items = ObservableBuffer[String]()
  }

  val contentPane = new VBox {
    spacing = 6
    padding = Insets(6)
    children = List(recipeLabel, recipeText, commandLabel, commandText, eventLabel, eventList)
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
    recipeText.text = recipe.toString
    commandText.text = ""
    eventList.items.get().clear()
    Brewery.brew(recipe)
  }

  commandProperty.onChange { (_, _, newCommand) =>
    commandText.text = s"Command: ${newCommand.getClass.getSimpleName}, Batch: ${newCommand.batch}, Executed: ${newCommand.executed}"
  }

  eventProperty.onChange { (_, _, newEvent) =>
    eventList.items.get().add(s"Event: ${newEvent.getClass.getSimpleName}, Batch: ${newEvent.batch}, Executed: ${newEvent.completed}")
    newEvent match {
      case Brewed(_) => brewButton.disable = false
      case _ =>
    }
  }
}