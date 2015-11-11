package app

import command.Command
import domain.{Recipe, IPA}
import event.{Brewed, Event}
import state.State
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

object Simulator extends JFXApp {
  val commandProperty = new ObjectProperty[Command]()
  val stateProperty = new ObjectProperty[State]()
  val eventProperty = new ObjectProperty[Event]()
  Brewery.register(commandProperty, stateProperty, eventProperty)

  val brewButton = new Button {
    text = "Brew"
  }

  val toolbar = new ToolBar {
    content = List(brewButton)
  }

  val recipeLabel = new Label {
    text = "Receipe:"
  }

  val recipeList =  new ListView[String] {
    prefHeight = 260
    items = ObservableBuffer[String]()
  }

  val commandLabel = new Label {
    text = "Commands:"
  }

  val commandText = new Text {
    wrappingWidth = 600
  }

  val stateLabel = new Label {
    text = "States:"
  }

  val stateList = new ListView[String] {
    prefHeight = 260
    items = ObservableBuffer[String]()
  }

  val eventLabel = new Label {
    text = "Events:"
  }

  val eventList = new ListView[String] {
    prefHeight = 280
    items = ObservableBuffer[String]()
  }

  val contentPane = new VBox {
    spacing = 6
    padding = Insets(6)
    children = List(recipeLabel, recipeList, commandLabel, commandText, stateLabel, stateList, eventLabel, eventList)
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
    recipeList.items.get().clear()
    commandText.text = ""
    stateList.items.get().clear()
    eventList.items.get().clear()
    listRecipe(recipe)
    Brewery.brew(recipe)
  }

  def listRecipe(recipe: Recipe): Unit = {
    val list = recipeList.items.get()
    list.add(s"Name: ${recipe.name} Style: ${recipe.style}")
    list.add(s"IBU: ${recipe.ibu} Color: ${recipe.color} ABV: ${recipe.abv}")
    list.add(s"Gravity { original: ${recipe.gravity.original} specific: ${recipe.gravity.specific} final: ${recipe.gravity.finished} }")
  }

  commandProperty.onChange { (_, _, newCommand) =>
    commandText.text = s"${newCommand.getClass.getSimpleName}, Batch: ${newCommand.batch}, Executed: ${newCommand.executed}"
  }

  stateProperty.onChange { (_, _, newState) =>
    stateList.items.get().add(s"${newState.getClass.getSimpleName}, Batch: ${newState.batch}, Started: ${newState.started}")
  }

  eventProperty.onChange { (_, _, newEvent) =>
    eventList.items.get().add(s"${newEvent.getClass.getSimpleName}, Batch: ${newEvent.batch}, Occurred: ${newEvent.occurred}")
    newEvent match {
      case Brewed(batch) => brewButton.disable = false
      case _ =>
    }
  }
}