package simulation.app

import simulation.command.Command
import simulation.domain.{IPA, Recipe}
import simulation.event.{Brewed, Event}
import simulation.state.State
import simulation.system.Brewery

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
  sys.addShutdownHook(Brewery.terminate())

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
    prefHeight = 230
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
    prefHeight = 240
    items = ObservableBuffer[String]()
  }

  val eventLabel = new Label {
    text = "Events:"
  }

  val eventList = new ListView[String] {
    prefHeight = 260
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
      Brewery.terminate()
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

  def listRecipe(recipe: Recipe): Unit = {
    val list = recipeList.items.get()
    list.add(s"Name: ${recipe.name} Style: ${recipe.style}")
    list.add(s"IBU: ${recipe.ibu} Color: ${recipe.color} ABV: ${recipe.abv}")
    list.add(s"Gravity( original: ${recipe.gravity.original} specific: ${recipe.gravity.specific} final: ${recipe.gravity.finished})")
    recipe.adjuncts.foreach { i => list.add(i.toString) }
    recipe.malts.foreach { i => list.add(i.toString) }
    recipe.hops.foreach { i => list.add(i.toString) }
    list.add(s"Yeast( kind: ${recipe.yeast.kind} amount: ${recipe.yeast.amount}${recipe.yeast.as})")
    list.add(s"Water( gallons: ${recipe.water.gallons}g boil size: ${recipe.water.boilSizeInGallons}g boil time: ${recipe.water.boilTimeInMinutes}m batch size: ${recipe.water.batchSizeInGallons}g)")
    list.add(s"Primary Fermentation ( days: ${recipe.primary.days} degrees: ${recipe.primary.degrees})")
    list.add(s"Secondary Fermentation ( days: ${recipe.primary.days} degrees: ${recipe.primary.degrees})")
    list.add(s"Mash: ${recipe.mash}")
    list.add(s"Boil: ${recipe.boil}")
    list.add(s"Cool: ${recipe.cool}")
    list.add(s"Ferment: ${recipe.ferment}")
    list.add(s"Condition: ${recipe.condition}")
    list.add(s"Bottle: ${recipe.bottle}")
    list.add(s"Keg: ${recipe.keg}")
    list.add(s"Cask: ${recipe.cask}")
  }
}