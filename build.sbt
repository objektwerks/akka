name := "objektwerks.akka"

version := "0.1"

scalaVersion := "2.11.7"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

libraryDependencies ++= {
  val akkaVersion = "2.4.0-RC3"
  Seq(
    "com.typesafe.akka" % "akka-actor_2.11" % akkaVersion,
    "com.typesafe.akka" % "akka-cluster_2.11" % akkaVersion,
    "com.typesafe.akka" % "akka-persistence_2.11" % akkaVersion,
    "com.typesafe.akka" % "akka-slf4j_2.11" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "org.iq80.leveldb" % "leveldb" % "0.7",
    "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
    "org.scalafx" % "scalafx_2.11" % "8.0.40-R8",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "com.typesafe.akka" % "akka-testkit_2.11" % akkaVersion % "test",
    "org.scalatest" % "scalatest_2.11" % "2.2.5" % "test"
  )
}

unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/ext/jfxrt.jar"))

scalacOptions ++= Seq(
  "-language:postfixOps",
  "-language:implicitConversions",
  "-language:reflectiveCalls",
  "-language:higherKinds",
  "-feature",
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Xfatal-warnings"
)

javaOptions += "-server -Xss1m -Xmx2g"
javaOptions in run += "-agentlib:TakipiAgent"

fork in run := true
fork in test := true

run in Compile <<= Defaults.runTask(fullClasspath in (Compile, run), mainClass in (Compile, run), runner in (Compile, run))
