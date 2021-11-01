package books

package object nytimes {
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


}
