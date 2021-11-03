package books.http

import books.TestCommons
import books.models.Book
import books.nyt.NytClient
import books.service.BookCachedProvider
import cats.effect.IO
import io.finch.Input
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.flatspec._
import org.scalatest.matchers._
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.duration._

class BookApiSpec extends AnyFlatSpec with should.Matchers with MockitoSugar with TestCommons {

  private val testCacheTTL = 10.millis

  private trait Components {
    val clientMock: NytClient = mock[NytClient]
    val cachedProvider = new BookCachedProvider(clientMock, testCacheTTL)
    val bookApi = new BookApi(cachedProvider)
  }


  private def mockNytAutorToBooks(clientMock: NytClient, author: String, booksFilename: String) = {
    when(clientMock.reviews(author)).thenReturn(
      IO.fromEither(decodeResource(booksFilename))
    )
  }

  behavior of classOf[BookApi].getSimpleName

  it should "tell everyone that service has a good health" in new Components {
    bookApi.healthcheck(Input.get("/")).awaitValueUnsafe().get shouldBe  "OK"
  }


  it should "retrieve books by author and year" in new Components {
    val Author = "Yuval Noah Harari"
    mockNytAutorToBooks(clientMock, Author, "books/nyt/yuva-noah-harari-reviews.json")


    val AskForHarari = s"/me/books/list?author=$Author&year=2017"
    val expected = List(Book("Homo Deus","Yuval Noah Harari","SIDDHARTHA MUKHERJEE",2017))
    bookApi.findByAuthor(Input.get(AskForHarari)).awaitValueUnsafe() shouldBe Some(expected)
  }

  it should "retrieve books by author and support many years" in new Components {
    val Author = "Stephen King"
    mockNytAutorToBooks(clientMock, Author, "books/nyt/stephen-king-reviews.json")

    val expected = List(
      Book("The Outsider","Stephen King","VICTOR LAVALLE",2018),
      Book("Elevation","Stephen King","GILBERT CRUZ",2018)
    )
    val input = Input.get(s"/me/books/list?author=$Author&year=2017&year=2018")
    bookApi.findByAuthor(input).awaitValueUnsafe() shouldBe Some(expected)
  }


  it should "when search by author endpoint called it has to cache results with TTL when TTL is expired should call NY Times again" in new Components {
    val Author = "Stephen King"
    mockNytAutorToBooks(clientMock, Author, "books/nyt/stephen-king-reviews.json")

    val input = Input.get(s"/me/books/list?author=$Author&year=2017&year=2018")
    bookApi.findByAuthor(input).awaitValueUnsafe().get.size shouldBe 2
    verify(clientMock, times(1)).reviews(Author)

    // check that second call goes only to cache
    bookApi.findByAuthor(input).awaitValueUnsafe().get.size shouldBe 2
    verify(clientMock, times(1)).reviews(Author)

    // now we wait till cache is expired
    Thread.sleep(testCacheTTL.toMillis)

    // and check that we ask NYT again
    bookApi.findByAuthor(input).awaitValueUnsafe().get.size shouldBe 2
    verify(clientMock, times(2)).reviews(Author)
  }



}
