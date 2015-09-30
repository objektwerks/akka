package akka

import com.typesafe.config.{Config, ConfigFactory}

object Conf {
  val config: Config = ConfigFactory.load("test.conf")
}