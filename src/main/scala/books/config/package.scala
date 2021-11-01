package books

import pureconfig._
import pureconfig.generic.auto._

import scala.concurrent.duration.FiniteDuration
import scala.util.Try

package object config {
  case class NytConfig(apiKey: String, requestTimeout: FiniteDuration) {
    assert(apiKey.nonEmpty, "You have to specify New York Times API Key in order to run application, see: https://developer.nytimes.com/get-started")
  }

  case class AppConfig(cacheTTL: FiniteDuration, port: Int, apiRoot: String, nyt: NytConfig) {
    def internalApi = s"$apiRoot:$port"
  }

  def maybeConfig: Try[AppConfig] = Try(ConfigSource.default.loadOrThrow[AppConfig])
}
