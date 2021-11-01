package books.nytimes

import books.TestCommons
import books.nytimes.NytRootResponse
import org.specs2.mutable.Specification

class NytRootResponseSpec extends Specification with TestCommons {


  classOf[NytRootResponse].getSimpleName should {

    "parse short response" in {
      decodeResource("books/nytimes/yuva-noah-harari-reviews.json") must beRight.like { case r: NytRootResponse =>
        r.results.map(_.book_title) === Seq("Homo Deus", "21 Lessons for the 21st Century")
        r.num_results === 2
      }
    }

    "parse long response" in {
      decodeResource("books/nytimes/stephen-king-reviews.json") must beRight.like { case r: NytRootResponse =>
        r.num_results === 66
      }
    }
  }
}