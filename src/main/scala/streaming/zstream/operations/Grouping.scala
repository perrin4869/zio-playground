package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._

object Grouping extends ZIOAppDefault {
  val groupedResult: ZStream[Any, Nothing, Chunk[Int]] =
    ZStream.fromIterable(0 to 8).grouped(3)

  // Input:  0, 1, 2, 3, 4, 5, 6, 7, 8
  // Output: Chunk(0, 1, 2), Chunk(3, 4, 5), Chunk(6, 7, 8)

  val groupedWithinResult: ZStream[Any, Nothing, Chunk[Int]] =
    ZStream
      .fromIterable(0 to 10)
      .repeat(Schedule.spaced(1.seconds))
      .groupedWithin(30, 10.seconds)

  override def run = for {
    _ <- print("groupedResult: ") *> groupedResult
      .runForeachChunk(printLine(_))
    _ <- print("groupedWithinResult: ") *> groupedWithinResult
      .take(5)
      .runForeachChunk(printLine(_))
  } yield ()
}
