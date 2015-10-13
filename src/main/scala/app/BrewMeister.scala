package app

import command.Command
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

object BrewMeister extends JFXApp {
  val commandProperty = new ObjectProperty[Command]()
  val eventProperty = new ObjectProperty[Event]()
  Brewery.register(commandProperty, eventProperty)

  val brewButton = new Button {
    text = "Brew"
    disable = false
  }

  val toolbar = new ToolBar {
    content = List(brewButton)
  }

  val recipeLabel = new Label {
    text = "Receipe"
  }

  val recipeText = new Text

  val commandLabel = new Label {
    text = "Commands"
  }

  val commandList = new ListView[String] {
    prefHeight = 100
    items = ObservableBuffer[String]()
  }

  val eventLabel = new Label {
    text = "Events"
  }

  val eventList = new ListView[String] {
    prefHeight = 400
    items = ObservableBuffer[String]()
  }

  val contentPane = new VBox {
    spacing = 6
    padding = Insets(6)
    children = List(recipeLabel, recipeText, commandLabel, commandList, eventLabel, eventList)
  }

  val appPane = new VBox {
    prefWidth = 800
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
    commandList.items.get().clear()
    eventList.items.get().clear()
    Brewery.brew(recipe)
  }

  commandProperty.onChange { (_, _, newCommand) =>
    commandList.items.get().add(newCommand.toString)
  }

  eventProperty.onChange { (_, _, newEvent) =>
    eventList.items.get().add(newEvent.toString)
    newEvent match {
      case event: Brewed => brewButton.disable = false
    }
  }
}