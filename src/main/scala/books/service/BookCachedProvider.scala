package books.service

import books.models.Book
import books.nytimes.{NycBook, NycTimesClient}
import cats.effect._
import scalacache._
import scalacache.caffeine._
import scalacache.memoization._

import java.time.LocalDate
import scala.concurrent.duration._

class BookCachedProvider(client: NycTimesClient, cacheTTL: FiniteDuration = 3.minutes) {
  implicit val cache: Cache[Seq[Book]] = CaffeineCache[Seq[Book]]
  implicit val mode: Mode[IO] = scalacache.CatsEffect.modes.async

  // NOTE: NY Times API does not have publisher, so the closest filed is "byline", of course it's not correct!
  private def nycBook2domain(b: NycBook): Book =
    Book(b.book_title, b.book_author, b.byline, LocalDate.parse(b.publication_dt).getYear)

  def findByAuthorAndYears(author: String, years: Set[Int]): IO[Seq[Book]] = memoizeF[IO, Seq[Book]](Some(cacheTTL)) {
    client.reviews(author).map { response =>
      val converted = response.results.map(nycBook2domain)
      if(years.isEmpty) converted
      else converted.filter(b => years.contains(b.year))
    }
  }

}
