package books

import pureconfig._
import pureconfig.generic.auto._

import scala.concurrent.duration.FiniteDuration
import scala.util.Try

package object config {
  case class NycConfig(apiKey: String, requestTimeout: FiniteDuration)

  case class AppConfig(cacheTTL: FiniteDuration, port: Int, apiRoot: String, nyc: NycConfig) {
    def internalApi = s"$apiRoot:$port"
  }

  def maybeConfig: Try[AppConfig] = Try(ConfigSource.default.loadOrThrow[AppConfig])
}
