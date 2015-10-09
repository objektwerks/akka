Akka Tests and Simulation
-------------------------
>This project contiains features tests and cluster simulation.

Simulation
----------
>The Akka cluster simulation is modeled from a brewery, to include a brew meister, brewer and classic staged brewing
process. The entirre process works as follows:

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

>Note how the 'brew' command flows through the brewing process until it transforms into a brewed event.
 
>During the brewing process, the masher on through to the bottler, emits a stage event to the brewer.

>The recipe, brew command and stage events are all displayed in the brew meister UI for a single brewing session.

Test
----
1. sbt clean test

Run
---
1. sbt clean compile run

Output
------
1. ./target/output
