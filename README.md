Akka
----
>This project contains a (1) word count simulator; (2) brewery simulator; and (3)feature tests.

Words
-----
>Akka cluster master-worker distributed app that counts words. Cluster topology:
* seed nodes - 2
* master nodes - 1
* worker nodes - 2

Brewery
-------
>Akka cluster that visually simulates a beer brewing process:

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

>1 message, 1 command, 8 states and 9 events.

Test
----
1. sbt clean test

Run
---
1. sbt clean compile run
2. select app number

Run Main
--------
1. sbt "runMain cluster.SeedNode words-seed-node.conf 2151 words"
2. sbt "runMain cluster.SeedNode words-seed-node.conf 2152 words"
3. sbt "runMain words.WorkerNode worker.conf 2153 words"
4. sbt "runMain words.WorkerNode worker.conf 2154 words"
5. sbt "runMain words.MasterNode master.conf 2155 words"

Output
------
1. target/output