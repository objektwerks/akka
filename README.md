Akka Tests and Cluster Simulation
---------------------------------
>This project contains feature tests as well as a cluster simulation.

Simulation
----------
>The Akka cluster simulation is modeled from a brewery, to include a brew meister, brewer and staged brewing process,
which works as follows:

1. brew meister --- recipe ---> brewery
2. brewery --- recipe ---> brewer
    1. brewer --- brew ---> masher
    2. masher --- brew ---> boiler  | masher --- mashed ---> brewer
    3. boiler --- brew ---> cooler  | boiler --- boiled ---> brewer
    4. cooler --- brew ---> fermenter   | cooler --- cooled ---> brewer
    5. fermenter --- brew ---> conditioner  | fermenter --- fermented ---> brewer
    6. conditioner --- brew ---> bottler    | conditioner --- conditioned ---> brewer
    7. bottler --- brewed ---> brewer   | bottler --- bottled ---> brewer
3. brewer --- brewed ---> brewery
4. brewery --- brewed ---> brew meister

>Note how the brew command flows through the brewing process until it's transformed into a brewed event.
 
>During the brewing process, the masher on through to the bottler, emits a stage event to the brewer, which is
forwarded to the brewery and brew meister.

>The recipe, brew command and stage events are displayed in the brew meister UI for a single brewing session.

>The relationships between the brew meister, brewery and brewer may seem odd. And they are in the real world. In this
simulation, however, the brew meister is the ScalaFX UI; the brewery is an actor system; and the brewer is the principle
actor.

Test
----
1. sbt clean test

Run
---
1. sbt clean compile run

Output
------
1. ./target/output
