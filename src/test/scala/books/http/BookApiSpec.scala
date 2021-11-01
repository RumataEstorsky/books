package books.http

import books.TestCommons
import books.nytimes.NycTimesClient
import books.service.BookCachedProvider
import cats.effect.IO
import io.finch.Input
import org.mockito.MockitoSugar
import org.specs2.mutable.Specification

import scala.concurrent.duration._
import scala.io.Source

class BookApiSpec extends Specification with MockitoSugar with TestCommons {

  private val clientMock = mock[NycTimesClient]
  private val cachedProvider = new BookCachedProvider(clientMock, 50.millis)
  private val bookApi = new BookApi(cachedProvider)

  classOf[BookApi].getSimpleName should {

    "tell everyone that service has a good health" in {
      bookApi.healthcheck(Input.get("/")).awaitValueUnsafe() === Some("OK")
    }

    "return books by author" in {

      decodeResource("books/nyctimes/yuva-noah-harari-reviews.json").map { response =>
        when(clientMock.reviews("Yuval Noah Harari")).thenReturn(IO.pure(response))
      }

      val AskForHarari = "http://localhost:8081/me/books/list" // ?author=Yuval%20Noah%20Harari
      bookApi.findByAuthor(Input.get(AskForHarari)).awaitValueUnsafe() === Some("OK")
    }
  }
}
