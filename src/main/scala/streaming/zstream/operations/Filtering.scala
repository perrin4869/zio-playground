package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._

object Filtering extends ZIOAppDefault {
  val s1 = ZStream.range(1, 11).filter(_ % 2 == 0)
  // Output: 2, 4, 6, 8, 10

  // The `ZStream#withFilter` operator enables us to write filter in for-comprehension style
  val s2 = for {
    i <- ZStream.range(1, 11).take(10)
    if i % 2 == 0
  } yield i
  // Output: 2, 4, 6, 8, 10

  val s3 = ZStream.range(1, 11).filterNot(_ % 2 == 0)
  // Output: 1, 3, 5, 7, 9

  override def run = for {
    _ <- print("s1: ") *> s1
      .runForeachChunk(printLine(_))
    _ <- print("s2: ") *> s2
      .runForeachChunk(printLine(_))
    _ <- print("s3: ") *> s3
      .runForeachChunk(printLine(_))
  } yield ()
}
