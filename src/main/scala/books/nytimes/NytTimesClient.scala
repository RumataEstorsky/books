package books.nytimes

import books.utils.IOLogging
import cats.effect.{ContextShift, IO}
import com.twitter.finagle.http.{Request, RequestBuilder}
import io.circe.generic.auto._
import io.circe.parser.decode
import io.github.felixbr.finagle.core.effect.TwitterDurationConversions._
import io.github.felixbr.finagle.http.effect.client._

import scala.concurrent.duration._

class NytTimesClient(apiKey: String, requestTimeout: FiniteDuration)(implicit ev: ContextShift[IO]) extends IOLogging {
  import NytTimesClient._



  private val httpClient = FinagleHttpClientBuilder[IO]
    .withUpdatedConfig(_.withRequestTimeout(requestTimeout).withTls(Host))
    .serviceResource(s"$Host:443")

  def reviews(author: String): IO[NytRootResponse] = httpClient.use { srv =>
    val request = RequestBuilder()
      .url(Request(s"https://$Host/svc/books/v3/reviews.json", "author" -> author, "api-key" -> apiKey).uri)
      .buildGet()

    for {
      body <- srv(request)
      result <- IO.fromEither(decode[NytRootResponse](body.contentString))
      _   <- log.info(s"Got from NewYork Times results: ${result.num_results}")
    } yield result
  }
}

object NytTimesClient {
  val Host = "api.nytimes.com"
}
