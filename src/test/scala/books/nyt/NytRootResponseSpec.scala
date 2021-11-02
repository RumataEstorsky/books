package books.nyt

import books.TestCommons
import org.scalatest._
import flatspec._
import matchers._
import io.circe.generic.auto._
import io.circe.parser.decode

class NytRootResponseSpec extends AnyFlatSpec with should.Matchers with EitherValues with TestCommons {

  behavior of classOf[NytRootResponse].getSimpleName

  it should "parse short result response" in {
    val r = decodeResource("books/nyt/yuva-noah-harari-reviews.json").toTry.get
    r.results.map(_.book_title) shouldBe Seq("Homo Deus", "21 Lessons for the 21st Century")
    r.num_results shouldBe 2
  }

  it should "parse long result response" in {
    decodeResource("books/nyt/stephen-king-reviews.json").toTry.get.num_results shouldBe 66
  }

  it should "parse failure response" in {
    val expected = NytFault(Fault("Invalid ApiKey", Detail("oauth.v2.InvalidApiKey")))
    decode[NytFault](r("books/nyt/fault.json")) shouldBe Right(expected)
  }

}