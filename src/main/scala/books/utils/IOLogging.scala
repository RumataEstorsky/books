package books.utils

import cats.effect.IO
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

trait IOLogging {
  implicit def log: Logger[IO] = Slf4jLogger.getLogger[IO]
}
