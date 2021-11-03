package books.nyt

import books.TestCommons
import books.models.Book
import cats.effect._
import com.twitter.finagle.http.Status.Ok
import com.twitter.finagle.stats.InMemoryStatsReceiver
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.net.URL
import scala.concurrent.ExecutionContext.global
import scala.concurrent.duration._

class NytClientSpec extends AnyFlatSpec
  with should.Matchers
  with BeforeAndAfterEach
  with TestCommons {

  private trait TestNytHttpsServer {
    lazy val server: ClientAndServer = ClientAndServer.startClientAndServer(6464)
  }


  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  private val client = new NytClient(
    new URL("http://localhost:6464/svc/books/v3/reviews.json"),
    "secret",
    100.millis,
    new InMemoryStatsReceiver)(cs)

  behavior of classOf[NytClient].getSimpleName

  it should "retrieve and parse results from NY Times API / book reviews" in new TestNytHttpsServer {
    val author = "Yuval Noah Harari"
    server.when(request()
      .withMethod("GET")
      .withPath("/svc/books/v3/reviews.json")
      .withQueryStringParameter("author", author)
      .withQueryStringParameter("api-key", "secret")
    )

      .respond(
        response()
          .withStatusCode(200)
          .withBody(r("books/nyt/yuva-noah-harari-reviews.json"))
      )

    val expected = NytRootResponse(
      status = "OK",
      copyright = "Copyright (c) 2021 The New York Times Company.  All Rights Reserved.",
      num_results = 2,
      results = Seq(
        NytBook("2017-03-13", "SIDDHARTHA MUKHERJEE", "Homo Deus", author),
        NytBook("2018-09-04", "BILL GATES", "21 Lessons for the 21st Century", author),
      )
    )

    client.reviews(author).unsafeRunSync() shouldBe expected

  }
}