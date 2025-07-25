package il.co.dotcore.zio.playground.streaming.zsink.leftovers

import zio._
import zio.stream._
import zio.Console._

object Collecting extends ZIOAppDefault {

  val s1: ZIO[Any, Nothing, (Chunk[Int], Chunk[Int])] =
    ZStream(1, 2, 3, 4, 5).run(
      ZSink.take(3).collectLeftover
    )
  // Output: (Chunk(1, 2, 3), Chunk(4, 5))

  val s2: ZIO[Any, Nothing, (Option[Int], Chunk[Int])] =
    ZStream(1, 2, 3, 4, 5).run(
      ZSink.head[Int].collectLeftover
    )
  // Output: (Some(1), Chunk(2, 3, 4, 5))

  override def run = for {
    _ <- s1.debug
    _ <- s2.debug
  } yield ()
}
