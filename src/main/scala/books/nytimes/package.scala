package books

package object nytimes {
  case class NycBook(
                      publication_dt: String,
                      byline: String,
                      book_title: String,
                      book_author: String,
                    )

  case class NycRootResponse(
                              status: String,
                              copyright: String,
                              num_results: Int,
                              results: Seq[NycBook]
                            )
}
