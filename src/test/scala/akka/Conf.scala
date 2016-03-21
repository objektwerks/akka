package akka

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object Conf {
  val config = ConfigFactory.load("test-akka.conf")
  val log = LoggerFactory.getLogger(this.getClass)
}