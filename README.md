Akka Tests and Cluster Simulation
---------------------------------
>This project contains feature tests and a cluster simulation.

Simulation
----------
>The Akka cluster simulation models a brewing process as follows:

1. app --- recipe ---> brewery
2. brewery --- recipe ---> brewer
    a. brewer --- brew ---> masher
    b. masher --- brew ---> boiler
        * masher --- mashing ~> mashed ---> brewer
    c. boiler --- brew ---> cooler
        * boiler --- boiling ~> boiled ---> brewer
    c. cooler --- brew ---> fermenter
        * cooler --- cooling ~> cooled ---> brewer
    e. fermenter --- brew ---> conditioner
        * fermenter --- fermenting ~> fermented ---> brewer
    f. conditioner --- brew ---> bottler & kegger & casker
        * conditioner --- conditioning ~> conditioned ~> brewed ---> brewer
    f. bottler --- bottling ~> bottled ---> brewer
    h. kegger --- kegging ~> kegged ---> brewer
    i. casker --- casking ~> casked ---> brewer
3. brewer --- brewed ---> brewery
4. brewery --- brewed ---> app

>The brew command flows through the brewing process, during which brewing events are emitted and displayed in the Simulator.

* Message: x --- m ---> y represents a message exchange.
* Event: x --- e(1) ~> e(n) ---> y represents an event exchange.

>TODO: Refactor Simulator with animations.

Test
----
1. sbt clean test

Run
---
1. sbt clean compile run

Output
------
1. ./target/output