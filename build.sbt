name := "akka"
organization := "objektwerks"
version := "0.1-SNAPSHOT"
scalaVersion := "2.13.1"
libraryDependencies ++= {
  val akkaVersion = "2.6.4"
  val openjfxVersion = "14"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "org.iq80.leveldb" % "leveldb" % "0.12",
    "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
    "org.scalafx" %% "scalafx" % "12.0.2-R18",
    "org.openjfx" % "javafx-base" % openjfxVersion,
    "org.openjfx" % "javafx-controls" % openjfxVersion,
    "org.openjfx" % "javafx-fxml" % openjfxVersion,
    "org.openjfx" % "javafx-graphics" % openjfxVersion,
    "org.openjfx" % "javafx-media" % openjfxVersion,
    "org.openjfx" % "javafx-swing" % openjfxVersion,
    "org.openjfx" % "javafx-web" % openjfxVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
    "org.scalatest" %% "scalatest" % "3.0.8" % Test
  )
}
scalacOptions ++= Seq("-target:jvm-11")
