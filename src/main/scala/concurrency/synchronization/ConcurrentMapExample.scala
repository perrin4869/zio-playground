package il.co.dotcore.zio.playground

import zio._
import zio.concurrent.ConcurrentMap

object ConcurrentMapExample extends ZIOAppDefault {
  def run =
    for {
      map <- ConcurrentMap.make(("foo", 0), ("bar", 1), ("baz", 2))
      _ <- ZIO.foreachParDiscard(1 to 100)(_ =>
        map.computeIfPresent("foo", (_, v) => v + 1)
      )
      _ <- map.get("foo").debug("The final value of foo is")
    } yield ()
}
