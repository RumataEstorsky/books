package books

import pureconfig._
import pureconfig.generic.auto._

import scala.concurrent.duration.FiniteDuration
import scala.util.Try

package object config {
  case class NycConfig(apiKey: String, requestTimeout: FiniteDuration) {
    assert(apiKey.nonEmpty, "You have to specify NYC Times API Key in order to run application, see: https://developer.nytimes.com/get-started")
  }

  case class AppConfig(cacheTTL: FiniteDuration, port: Int, apiRoot: String, nyc: NycConfig) {
    def internalApi = s"$apiRoot:$port"
  }

  def maybeConfig: Try[AppConfig] = Try(ConfigSource.default.loadOrThrow[AppConfig])
}
