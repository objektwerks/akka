name := "akka"
organization := "objektwerks"
version := "0.1-SNAPSHOT"
scalaVersion := "2.13.16"
libraryDependencies ++= {
  val akkaVersion = "2.6.21" // Don't upgrade due to BUSL 1.1!
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "org.scalafx" %% "scalafx" % "24.0.0-R35",
    "ch.qos.logback" % "logback-classic" % "1.5.18",
    "org.iq80.leveldb" % "leveldb" % "0.12" % Test,
    "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8" % Test,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
    "org.scalatest" %% "scalatest" % "3.2.19" % Test
  )
}
