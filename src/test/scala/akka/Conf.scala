package akka

import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.{Logger, LoggerFactory}

object Conf {
  val config: Config = ConfigFactory.load("test.conf")
  val log: Logger = LoggerFactory.getLogger(this.getClass)
}