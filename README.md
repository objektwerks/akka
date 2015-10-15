Akka Tests and Cluster Simulation
---------------------------------
>This project contains feature tests and a cluster simulation.

Simulation
----------
>The Akka cluster simulation models a brewing process as follows:

1. app --- recipe ---> brewery
2. brewery --- recipe ---> brewer
    1. brewer --- brew ---> masher
    2. masher --- brew ---> boiler
        1. masher --- mashing ~> mashed ---> brewer
    3. boiler --- brew ---> cooler
        1. boiler --- boiling ~> boiled ---> brewer
    4. cooler --- brew ---> fermenter
        1. cooler --- cooling ~> cooled ---> brewer
    5. fermenter --- brew ---> conditioner
        1. fermenter --- fermenting ~> fermented ---> brewer
    6. conditioner --- brew ---> bottler & kegger & casker
        1. conditioner --- conditioning ~> conditioned ~> brewed ---> brewer
    7. bottler --- bottling ~> bottled ---> brewer
    8. kegger --- kegging ~> kegged ---> brewer
    9. casker --- casking ~> casked ---> brewer
3. brewer --- brewed ---> brewery
4. brewery --- brewed ---> app

>The brew command flows through the brewing process, during which brewing events are emitted and displayed in the app.

Test
----
1. sbt clean test

Run
---
1. sbt clean compile run

Output
------
1. ./target/output