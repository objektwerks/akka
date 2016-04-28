name := "objektwerks.akka"
version := "0.1"
scalaVersion := "2.11.8"
ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }
libraryDependencies ++= {
  val akkaVersion = "2.4.4"
  val kamonVersion = "0.6.1"
  Seq(
    "com.typesafe.akka" % "akka-actor_2.11" % akkaVersion,
    "com.typesafe.akka" % "akka-stream_2.11" % akkaVersion,
    "com.typesafe.akka" % "akka-persistence_2.11" % akkaVersion,
    "com.typesafe.akka" % "akka-slf4j_2.11" % akkaVersion,
    "com.typesafe.akka" % "akka-cluster_2.11" % akkaVersion,
    "com.typesafe.akka" % "akka-cluster-metrics_2.11" % akkaVersion,
    "com.typesafe.akka" % "akka-cluster-tools_2.11" % akkaVersion,
    "com.typesafe.akka" % "akka-contrib_2.11" % akkaVersion,
    "com.typesafe.akka" % "akka-http-experimental_2.11" % akkaVersion,
    "com.typesafe.akka" % "akka-http-spray-json-experimental_2.11" % akkaVersion,
    "io.kamon" % "sigar-loader" % "1.6.6-rev002",
    "io.kamon" % "kamon-akka_2.11" % kamonVersion,
    "io.kamon" %% "kamon-statsd" % kamonVersion,
    "org.iq80.leveldb" % "leveldb" % "0.7",
    "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
    "org.scalafx" % "scalafx_2.11" % "8.0.60-R9",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "com.typesafe.akka" % "akka-testkit_2.11" % akkaVersion % "test",
    "com.typesafe.akka" % "akka-http-testkit-experimental_2.11" % "2.4.2-RC3" % "test",
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
aspectjSettings
javaOptions in runMain <++= AspectjKeys.weaverOptions in Aspectj
fork := true
run in Compile <<= Defaults.runTask(fullClasspath in (Compile, run), mainClass in (Compile, run), runner in (Compile, run))