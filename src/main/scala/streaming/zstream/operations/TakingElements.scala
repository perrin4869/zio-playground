package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._

object TakingElements extends ZIOAppDefault {
  val stream = ZStream.iterate(0)(_ + 1)
  val s1 = stream.take(5)
  val s2 = stream.takeWhile(_ < 5)
  val s3 = stream.takeUntil(_ == 5)
  val s4 = s3.takeRight(3)

  override def run = for {
    _ <- print("s1: ") *> s1
      .runForeachChunk(printLine(_))
    _ <- print("s2: ") *> s2
      .runForeachChunk(printLine(_))
    _ <- print("s3: ") *> s3
      .runForeachChunk(printLine(_))
    _ <- print("s4: ") *> s4
      .runForeachChunk(printLine(_))
  } yield ()
}
