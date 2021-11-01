package books.http


import books.models.Book
import books.service.BookCachedProvider
import cats.effect.IO
import io.finch._

class BookApi(service: BookCachedProvider)
  extends Endpoint.Module[IO]
    with ExecutionMetrics
    with ErrorHandling {

  case class BooksSearch(author: String, years: List[Int])

  private val booksSearchParams = (param[String]("author") :: params[Int]("year")).as[BooksSearch]

  val endpoints = Bootstrap
    .serve[Text.Plain](healthcheck)
    .serve[Application.Json](findByAuthor)


  def findByAuthor: Endpoint[IO, Seq[Book]] = {
    get("me" :: "books" :: "list" :: booksSearchParams) { search: BooksSearch =>
      service.findByAuthorAndYears(
        author = search.author.trim,
        years = search.years.toSet
      ).map(Ok)
    }.handle {
      case e: Exception => BadRequest(e)
    }
  }

  def healthcheck: Endpoint[IO, String] = get(pathEmpty) {
    Ok("OK")
  }


}

