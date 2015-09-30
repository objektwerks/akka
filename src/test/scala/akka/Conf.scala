package akka

import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.{LoggerFactory, Logger}

object Conf {
  val config: Config = ConfigFactory.load("test.conf")
  val log: Logger = LoggerFactory.getLogger(this.getClass)
}