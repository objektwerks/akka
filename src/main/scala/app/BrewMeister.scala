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

  val toolbar = new ToolBar {
    content = List(brewButton)
  }

  val recipeLabel = new Label {
    text = "Receipe"
  }

  val recipeText = new Text {
    wrappingWidth = 600
  }

  val brewedLabel = new Label {
    text = "Brewed"
  }

  val brewedText = new Text {
    wrappingWidth = 600
  }

  val contentPane = new VBox {
    spacing = 6
    padding = Insets(6)
    children = List(recipeLabel, recipeText, brewedLabel, brewedText)
  }

  val appPane = new VBox {
    prefHeight = 200
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
    Brewery.brew(recipe)
  }

  brewedProperty.onChange({
    brewButton.disable = false
    brewedText.text = brewedProperty.value.toString
  })
}