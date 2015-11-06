Akka Tests and Cluster Simulation
---------------------------------
>This project contains Akka feature tests and an Akka cluster visual simulation.

Simulation
----------
>The Akka cluster visual simulation models a brewing process as follows:

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

>The brew command flows through the brewing process, during which brewing states and events are emitted and displayed in the Simulator.

* (m) = message
* (c) = command
* (s) = state
* (e) = event

>1 command, 8 states and 9 events.

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

1. Schedular, also referred to as Program ( A, B, C, D ... )
2. Zone, also referred to as Station