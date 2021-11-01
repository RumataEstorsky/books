package books.nyctimes

import books.TestCommons
import books.nytimes.NycRootResponse
import org.specs2.mutable.Specification

class NycRootResponseSpec extends Specification with TestCommons {


  classOf[NycRootResponse].getSimpleName should {

    "parse short response" in {
      decodeResource("books/nyctimes/yuva-noah-harari-reviews.json") must beRight.like { case r: NycRootResponse =>
        r.results.map(_.book_title) === Seq("Homo Deus", "21 Lessons for the 21st Century")
        r.num_results === 2
      }
    }

    "parse long response" in {
      decodeResource("books/nyctimes/stephen-king-reviews.json") must beRight.like { case r: NycRootResponse =>
        r.num_results === 66
      }
    }
  }
}