package client

import domain.{IPA, Recipe}
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
  val recipeProperty = new ObjectProperty[Recipe]()
  val brewedProperty = new ObjectProperty[Brewed]()
  BreweryProxy.register(recipeProperty, brewedProperty)

  val brewButton = new Button {
    text = "Brew"
    onAction = { ae: ActionEvent => BreweryProxy.brew(IPA()) }
  }

  val statusBar = new Label

  val toolbar = new ToolBar {
    content = List(brewButton, new Separator(), statusBar)
  }

  val statusText = new Text

  val contentPane = new VBox {
    maxWidth = 400
    maxHeight = 400
    spacing = 6
    padding = Insets(6)
    children = List(statusText)
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

  recipeProperty.onChange({
    statusBar.text = "Brewing..."
    statusText.text = recipeProperty.value.toString
  })

  brewedProperty.onChange({
    statusBar.text = "Brewed!"
    statusText.text = brewedProperty.value.toString
  })
}