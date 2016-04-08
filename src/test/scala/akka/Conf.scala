package akka

import com.typesafe.config.ConfigFactory

object Conf {
  val config = ConfigFactory.load("test.conf")
}