package books

package object nyt {
  case class NytBook(
                      publication_dt: String,
                      byline: String,
                      book_title: String,
                      book_author: String,
                    )

  case class NytRootResponse(
                              status: String,
                              copyright: String,
                              num_results: Int,
                              results: Seq[NytBook]
                            )

  // Error response
  class NytApiError(reason: String) extends Exception(s"NY Times API Error: $reason")
  case class Detail (errorcode: String)
  case class Fault (faultstring: String, detail: Detail)
  case class NytFault(fault: Fault) {
    def toError: NytApiError = new NytApiError(s"(${fault.detail.errorcode}) ${fault.faultstring}")
  }




}
