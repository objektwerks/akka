package client

import domain.IPA
import event.Brewed

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.beans.property.ObjectProperty
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, Separator, ToolBar}
import scalafx.scene.layout.VBox
import scalafx.scene.text.Text

object App extends JFXApp {
  val brewedProperty = new ObjectProperty[Brewed]()
  BreweryProxy.register(brewedProperty)

  val brewButton = new Button {
    text = "Brew"
  }

  val statusBar = new Label

  val toolbar = new ToolBar {
    content = List(brewButton, new Separator(), statusBar)
  }

  val recipeLabel = new Label
  val recipeText = new Text

  val brewedLabel = new Label
  val brewedText = new Text

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
    title = "IPA Brewery"
    scene = new Scene {
      root = appPane
    }
  }

  brewButton.onAction = { ae: ActionEvent =>
    val recipe = IPA()
    statusBar.text = "Brewing..."
    recipeText.text = recipe.toString
    brewedText.text = ""
    BreweryProxy.brew(recipe)
  }

  brewedProperty.onChange({
    statusBar.text = "Brewed!"
    brewedText.text = brewedProperty.value.toString
  })
}