Akka Tests and Cluster Simulation
---------------------------------
>This project contains Akka feature tests and an Akka cluster visual simulation.

Simulation
----------
>The Akka cluster visual simulation models a brewing process as follows:

1. app --- recipe ---> brewery
2. brewery --- recipe ---> brewer
    1. brewer --- brew ---> masher
    2. masher --- brew ---> boiler
        * masher --- mashing ~> mashed ---> brewer
    3. boiler --- brew ---> cooler
        * boiler --- boiling ~> boiled ---> brewer
    4. cooler --- brew ---> fermenter
        * cooler --- cooling ~> cooled ---> brewer
    5. fermenter --- brew ---> conditioner
        * fermenter --- fermenting ~> fermented ---> brewer
    6. conditioner --- brew ---> bottler & kegger & casker
        * conditioner --- conditioning ~> conditioned ~> brewed ---> brewer
    7. bottler --- bottling ~> bottled ---> brewer
    8. kegger --- kegging ~> kegged ---> brewer
    9. casker --- casking ~> casked ---> brewer
3. brewer --- brewed ---> brewery
4. brewery --- brewed ---> app

>The brew command flows through the brewing process, during which brewing events are emitted and displayed in the Simulator.

* Message: x --- m ---> y represents a message exchange.
* Event: x --- e ~> e ---> y represents an event exchange.

>TODO: Implement AnimatedSimulator.

Test
----
1. sbt clean test

Run
---
1. sbt clean compile run

Output
------
1. target/output

Sprinkler System
----------------

Controller 1 --- controls ---> 1..4 Scheduler 1 --- schedules ---> 1 Timer 1 --- opens / closes ---> 1 Valve 1 --- activates / deactivates ---> 1 Zone

Schedular, also referred to as Program ( A, B, C, D ... )
Zone, also referred to as Station