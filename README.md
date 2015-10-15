Akka Tests and Cluster Simulation
---------------------------------
>This project contains feature tests and a cluster simulation.

Simulation
----------
>The Akka cluster simulation models a brewery as follows:

1. app --- recipe ---> brewery
2. brewery --- recipe ---> brewer
    1. brewer --- brew ---> masher
    2. masher --- brew ---> boiler  | masher --- mashing ~> mashed ---> brewer
    3. boiler --- brew ---> cooler  | boiler --- boiling ~> boiled ---> brewer
    4. cooler --- brew ---> fermenter   | cooler --- cooling ~> cooled ---> brewer
    5. fermenter --- brew ---> conditioner  | fermenter --- fermenting ~> fermented ---> brewer
    6. conditioner --- brew ---> bottler & casker   | conditioner --- conditioning ~> conditioned ~> brewed ---> brewer
    7. bottler --- bottling ~> bottled ---> brewer
    8. casker --- casking ~> casked ---> brewer
3. brewer --- brewed ---> brewery
4. brewery --- brewed ---> app

>The brew command flows through the brewing process, during which events are emitted and display in the app.

Test
----
1. sbt clean test

Run
---
1. sbt clean compile run

Output
------
1. ./target/output