package books.http

import books.nyt.NytClient
import books.service.BookCachedProvider
import io.finch.Input
import org.scalatest.flatspec._
import org.scalatest.matchers._
import org.scalatestplus.mockito.MockitoSugar
import scala.concurrent.duration._

class BookApiSpec extends AnyFlatSpec with should.Matchers with MockitoSugar {

  private val clientMock = mock[NytClient]
  private val cachedProvider = new BookCachedProvider(clientMock, 50.millis)
  private val bookApi = new BookApi(cachedProvider)


  behavior of classOf[BookApi].getSimpleName

  it should "tell everyone that service has a good health" in {
    bookApi.healthcheck(Input.get("/")).awaitValueUnsafe().get shouldBe  "OK"
  }


  it should "retrieve books by author" in {
    val AskForHarari = "http://localhost:8081/me/books/list" // ?author=Yuval%20Noah%20Harari
    bookApi.findByAuthor(Input.get(AskForHarari)) shouldBe Some("OK")
  }

}
