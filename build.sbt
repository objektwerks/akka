name := "akka"
organization := "objektwerks"
version := "0.1-SNAPSHOT"
scalaVersion := "2.12.8"
libraryDependencies ++= {
  val akkaVersion = "2.5.19"
  Seq(
    "com.typesafe.akka" % "akka-actor_2.12" % akkaVersion,
    "com.typesafe.akka" % "akka-persistence_2.12" % akkaVersion,
    "com.typesafe.akka" % "akka-slf4j_2.12" % akkaVersion,
    "org.iq80.leveldb" % "leveldb" % "0.10",
    "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
    "org.scalafx" % "scalafx_2.12" % "8.0.181-R13",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.akka" % "akka-testkit_2.12" % akkaVersion % "test",
    "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test"
  )
}
unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/ext/jfxrt.jar"))