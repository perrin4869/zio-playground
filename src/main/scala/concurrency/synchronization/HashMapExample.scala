package il.co.dotcore.myziotest

import zio._
import scala.collection.mutable

object HashMapExample extends ZIOAppDefault {

  def inc(ref: Ref[mutable.HashMap[String, Int]], key: String) =
    for {
      _ <- ref.get
      _ <- ref.update { map =>
        map.update(key, map.get(key).get + 1)
        map
      }
    } yield ()

  def run =
    for {
      ref <- Ref.make(mutable.HashMap(("foo", 0)))
      _ <- ZIO.foreachParDiscard(1 to 100)(_ => inc(ref, "foo"))
      _ <- ref.get.map(_.get("foo")).debug("The final value of foo is")
    } yield ()
}
