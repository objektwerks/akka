package app

import brewery.Brewery
import domain.IPA
import event.Brewed

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.beans.property.ObjectProperty
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.VBox
import scalafx.scene.text.Text

object BrewMeister extends JFXApp {
  val brewedProperty = new ObjectProperty[Brewed]()
  Brewery.register(brewedProperty)

  val brewButton = new Button {
    text = "Brew"
    disable = false
  }

  val brewingIndicator = new ProgressIndicator {
    prefWidth = 50
    progress = -1.0
    visible = false
  }

  val statusBar = new Label

  val toolbar = new ToolBar {
    content = List(brewButton, new Separator(), brewingIndicator, new Separator(), statusBar)
  }

  val recipeLabel = new Label {
    text = "Receipe"
  }

  val recipeText = new Text {
    wrappingWidth = 600
    maxWidth(600)
    maxHeight(200)
  }

  val brewedLabel = new Label {
    text = "Brewed"
  }

  val brewedText = new Text {
    wrappingWidth = 600
    maxWidth(600)
    maxHeight(200)
  }

  val contentPane = new VBox {
    maxWidth = 600
    maxHeight = 600
    spacing = 6
    padding = Insets(6)
    children = List(recipeLabel, recipeText, brewedLabel, brewedText)
  }

  val appPane = new VBox {
    maxWidth = 600
    maxHeight = 600
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
    brewingIndicator.visible = true
    brewButton.disable = true
    statusBar.text = "Brewing..."
    recipeText.text = recipe.toString
    brewedText.text = ""
    Brewery.brew(recipe)
  }

  brewedProperty.onChange({
    brewingIndicator.visible = false
    brewButton.disable = false
    statusBar.text = "Brewed!"
    brewedText.text = brewedProperty.value.toString
  })
}