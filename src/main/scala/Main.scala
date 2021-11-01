
import books.config._
import books.http.BookApi
import books.nytimes.NytTimesClient
import books.service.BookCachedProvider
import books.utils.IOLogging
import cats.effect.{ExitCode, IO, IOApp}
import com.twitter.finagle.Http
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch.circe._

object Main extends IOApp with IOLogging {

  override def run(args: List[String]): IO[ExitCode] = for {
    conf <- IO.fromTry(maybeConfig)
    _ <- log.info(s"Starting Book Search Service on ${conf.internalApi}...")
    client = new NytTimesClient(conf.nyt.apiKey, conf.nyt.requestTimeout)
    cachedProvider = new BookCachedProvider(client, conf.cacheTTL)
    bookApi = new BookApi(cachedProvider)
    server <- IO(Http.server.serve(conf.internalApi, bookApi.endpoints.toService))
    _ <- IO(Await.ready(server))
  } yield ExitCode.Success

}
