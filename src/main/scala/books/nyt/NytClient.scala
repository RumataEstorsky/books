package books.nyt

import books.utils.IOLogging
import cats.effect.{ContextShift, IO}
import com.twitter.finagle.http.Status.Ok
import com.twitter.finagle.http.{Request, RequestBuilder, Response}
import com.twitter.finagle.stats.{Counter, Stat, StatsReceiver}
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.parser.decode
import io.github.felixbr.finagle.core.effect.TwitterDurationConversions._
import io.github.felixbr.finagle.http.effect.client._

import java.net.URL
import scala.concurrent.duration._

class NytClient(rootUrl: URL, apiKey: String, requestTimeout: FiniteDuration, statsReceiver: StatsReceiver)(implicit ev: ContextShift[IO]) extends IOLogging {

  import NytClient._

  private val requestsSuccesses: Counter = statsReceiver.counter(s"$MetricsReviewsPrefix.successes-counter")
  private val requestsFailures: Counter = statsReceiver.counter(s"$MetricsReviewsPrefix.failures-counter")
  private val requestsLatency: Stat = statsReceiver.stat(s"$MetricsReviewsPrefix.request-latency")

  implicit val decodeRootResponse: Decoder[NytRootResponse] = deriveDecoder
  implicit val decodeNytBook: Decoder[NytBook] = deriveDecoder
  implicit val decodeNytFault: Decoder[NytFault] = deriveDecoder
  implicit val decodeFault: Decoder[Fault] = deriveDecoder
  implicit val decodeDetail: Decoder[Detail] = deriveDecoder

  private val httpClient = FinagleHttpClientBuilder[IO]
    .withUpdatedConfig { s =>
      s.withRequestTimeout(requestTimeout).withStatsReceiver(statsReceiver)
      if(rootUrl.getProtocol == "https") s.withTls(rootUrl.getHost) else s
    }.serviceResource(s"${rootUrl.getHost}:${rootUrl.getPort}")

  private def digest(response: Response): Either[Exception, NytRootResponse] =
    if (response.status == Ok) {
      requestsSuccesses.incr()
      decode[NytRootResponse](response.contentString)
    } else {
      requestsFailures.incr()
      decodeFailure(response.contentString)
    }

  // here always left, because `NytFault` it's already error in meaning of the client
  private def decodeFailure(s: String): Either[NytApiError, NytRootResponse] = decode[NytFault](s) match {
    case Left(e) => Left(new NytApiError(e.getMessage + ":" + s.take(500)))
    case Right(f) => Left(f.toError)
  }

  def reviews(author: String): IO[NytRootResponse] = Stat.time(requestsLatency) {
    httpClient.use { srv =>
      val request = RequestBuilder()
        .url(Request(rootUrl.toString, "author" -> author, "api-key" -> apiKey).uri)
        .buildGet()

      for {
        response <- srv(request)
        result <- IO.fromEither(digest(response))
        _ <- log.info(s"Got from NewYork Times results: ${result.num_results}")
      } yield result
    }
  }
}

object NytClient {
  private val MetricsReviewsPrefix = "nyt-times-client.reviews"
}
