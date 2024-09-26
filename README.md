Akka
----
>Akka feature tests, to include a brewery simulation using Akka and ScalaFx.

Test
----
1. sbt clean test

Run
---
1. sbt run

Brewery
-------
>Akka app that visually simulates a beer brewing process. See src/main/scala/akka/brewery directory for source.

>The Brew command, via Akka messaging, flows through each actor of the brewing process, during which brewing Command,
State and Event instances, subscribed to by the Brewer, are published via Akka event streaming and pushed to ScalaFx
property change listeners and displayed in the BrewMeister UI.

* (m) = message [1]
* (c) = command [1]
* (s) = state   [8]
* (e) = event   [9]

1. app --- recipe(m) ---> brewery
2. brewery --- recipe(m) ---> brewer
    1. brewer --- brew(c) ---> masher
    2. masher --- brew(c) ---> boiler
       * masher --- mashing(s) ---> brewer
       * masher --- mashed(e) ---> brewer
    3. boiler --- brew(c) ---> cooler
       * boiler --- boiling(s) ---> brewer
       * boiler --- boiled(e) ---> brewer
    4. cooler --- brew(c) ---> fermenter
       * cooler --- cooling(s) ---> brewer
       * cooler --- cooled(e) ---> brewer
    5. fermenter --- brew(c) ---> conditioner
       * fermenter --- fermenting(s) ---> brewer
       * fermenter --- fermented(e) ---> brewer
    6. conditioner --- brew(c) ---> bottler && kegger && casker
       * conditioner --- conditioning(s) ---> brewer
       * conditioner --- conditioned(e) ---> brewer
       * conditioner --- brewed(e) ---> brewer
    7. bottler --- bottling(s) ---> brewer
       * bottler --- bottled(e) ---> brewer
    8. kegger --- kegging(s) ---> brewer
       * kegger --- kegged(e) ---> brewer
    9. casker --- casking(s) ---> brewer
       * casker --- casked(e) ---> brewer
3. brewer --- brewed(e) ---> brewery
4. brewery --- brewed(e) ---> app

>See akka/brewmeister.png for a visual of the simulation.
