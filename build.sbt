enablePlugins(JlinkPlugin)

name := "akka"
organization := "objektwerks"
version := "0.1-SNAPSHOT"
scalaVersion := "2.13.2"
libraryDependencies ++= {
  val akkaVersion = "2.6.5"
  val openjfxVersion = "14"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "org.scalafx" %% "scalafx" % "12.0.2-R18",
    "org.openjfx" % "javafx-controls" % openjfxVersion,
    "org.openjfx" % "javafx-media" % openjfxVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.iq80.leveldb" % "leveldb" % "0.12" % Test,
    "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8" % Test,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
    "org.scalatest" %% "scalatest" % "3.0.8" % Test
  )
}
scalacOptions ++= Seq("-target:jvm-11")
jlinkModules := {
  jlinkModules.value :+ "jdk.unsupported"
}
jlinkIgnoreMissingDependency := JlinkIgnore.everything