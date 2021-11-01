package books

import books.nytimes.NycRootResponse
import io.circe
import io.circe.generic.auto._
import io.circe.parser.decode
import scala.io.Source

trait TestCommons {
  def r(filename: String): String = Source.fromResource(filename).mkString
  def decodeResource(filename: String): Either[circe.Error, NycRootResponse] = decode[NycRootResponse](r(filename))

}
