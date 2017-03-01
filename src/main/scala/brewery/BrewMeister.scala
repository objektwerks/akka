package brewery

import scalafx.application.{JFXApp, Platform}
import scalafx.beans.property.ObjectProperty
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.VBox
import scalafx.scene.text.Text

object BrewMeister extends JFXApp {
  val commandProperty = new ObjectProperty[Command]()
  val stateProperty = new ObjectProperty[State]()
  val eventProperty = new ObjectProperty[Event]()

  Brewery.register(commandProperty, stateProperty, eventProperty)
  sys.addShutdownHook(Brewery.close())

  val brewButton = new Button { text = "Brew" }
  val toolbar = new ToolBar { content = List(brewButton) }

  val recipeLabel = new Label { text = "Receipe:" }
  val recipeList =  new ListView[String] {
    prefHeight = 230
    items = ObservableBuffer[String]()
  }

  val commandLabel = new Label { text = "Commands:" }
  val commandText = new Text { wrappingWidth = 600 }

  val stateLabel = new Label { text = "States:" }
  val stateList = new ListView[String] {
    prefHeight = 240
    items = ObservableBuffer[String]()
  }

  val eventLabel = new Label { text = "Events:" }
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
  }

  brewButton.onAction = { _ =>
    commandText.text = ""
    stateList.items.get().clear()
    eventList.items.get().clear()
    val recipe = IPA()
    listRecipe(recipe)
    Platform.runLater(Brewery.brew(recipe))
  }

  commandProperty.onChange { (_, _, command) =>
    Platform.runLater(commandText.text = s"${command.getClass.getSimpleName}, Batch # ${command.batch}, Executed @ ${command.executed}")
  }

  stateProperty.onChange { (_, _, state) =>
    Platform.runLater(stateList.items.get().add(s"${state.getClass.getSimpleName}, Batch # ${state.batch}, Started @ ${state.started}"))
  }

  eventProperty.onChange { (_, _, event) =>
    Platform.runLater(eventList.items.get().add(s"${event.getClass.getSimpleName}, Batch # ${event.batch}, Occurred @ ${event.occurred}"))
  }

  def listRecipe(recipe: Recipe): Unit = {
    val list = recipeList.items.get()
    list.clear()
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