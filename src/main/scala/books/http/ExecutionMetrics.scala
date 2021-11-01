package books.http

import cats.effect.IO
import com.twitter.util.Time
import io.finch.Endpoint

trait ExecutionMetrics {
  val logging: Endpoint.Compiled[IO] => Endpoint.Compiled[IO] = compiled => {
    compiled.tapWithF { (req, res) =>
      IO(print(s"Request: $req\n")) *> IO(print(s"Response: $res\n")) *> IO.pure(res)
    }
  }

  val stats: Endpoint.Compiled[IO] => Endpoint.Compiled[IO] = compiled => {
    val now = IO(Time.now)
    Endpoint.Compiled[IO] { req =>
      for {
        start <- now
        traceAndResponse <- compiled(req)
        (trace, response) = traceAndResponse
        stop <- now
        _ <- IO(print(s"Response time: ${stop.diff(start)}. Trace: $trace\n"))
      } yield (trace, response)
    }
  }
}
