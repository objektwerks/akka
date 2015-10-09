package app

import brewery.Brewery
import domain.IPA
import event.{Brewed, Stage}

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
  val stageProperty = new ObjectProperty[Stage]()
  val brewedProperty = new ObjectProperty[Brewed]()
  Brewery.register(stageProperty, brewedProperty)

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

  val recipeText = new Text {
    wrappingWidth = 550
  }

  val brewedLabel = new Label {
    text = "Brewed"
  }

  val brewedText = new Text {
    wrappingWidth = 550
  }

  val stageLabel = new Label {
    text = "Stages"
  }

  val stageList = new ListView[String] {
    items = ObservableBuffer[String]()
  }

  val contentPane = new VBox {
    spacing = 6
    padding = Insets(6)
    children = List(recipeLabel, recipeText, brewedLabel, brewedText, stageLabel, stageList)
  }

  val appPane = new VBox {
    prefHeight = 450
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
    brewedText.text = ""
    stageList.items.get().clear()
    Brewery.brew(recipe)
  }

  stageProperty.onChange { (_, _, newValue) =>
    stageList.items.get().add(newValue.toString)
  }

  brewedProperty.onChange { (_, _, newValue) =>
    brewButton.disable = false
    brewedText.text = newValue.toString
  }
}