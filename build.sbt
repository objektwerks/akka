name := "akka"
organization := "objektwerks"
version := "0.1-SNAPSHOT"
scalaVersion := "2.12.8"
libraryDependencies ++= {
  val akkaVersion = "2.5.19"
  Seq(
    "com.typesafe.akka" % "akka-actor_2.12" % akkaVersion,
    "com.typesafe.akka" % "akka-actor-typed_2.12" % akkaVersion,
    "com.typesafe.akka" % "akka-persistence_2.12" % akkaVersion,
    "com.typesafe.akka" % "akka-slf4j_2.12" % akkaVersion,
    "org.iq80.leveldb" % "leveldb" % "0.10",
    "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
    "org.scalafx" % "scalafx_2.12" % "11-R16",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.akka" % "akka-testkit_2.12" % akkaVersion % "test",
    "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test"
  )
}
// Determine OS version of JavaFX binaries
lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ => throw new Exception("Unknown platform!")
}
lazy val javaFXModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
libraryDependencies ++= javaFXModules.map( m => "org.openjfx" % s"javafx-$m" % "11" classifier osName )