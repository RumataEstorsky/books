package books.nyt

import cats.effect._
import com.twitter.finagle.stats.InMemoryStatsReceiver
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.net.URL
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.global

class NytClientSpec extends AnyFlatSpec with should.Matchers  {

//  implicit val cs: ContextShift[IO] = IO.contextShift(global)
//  private val client = new NytClient(
//    new URL("http://localhost"),
//    "secret",
//    10.millis,
//    new InMemoryStatsReceiver)(cs)

  behavior of classOf[NytClient].getSimpleName

  it should "???" in {
    1 shouldBe 1
  }
}