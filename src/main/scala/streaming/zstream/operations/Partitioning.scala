package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._

object Partitioning extends ZIOAppDefault {
  val partitionResult: ZIO[
    Scope,
    Nothing,
    (ZStream[Any, Nothing, Int], ZStream[Any, Nothing, Int])
  ] =
    ZStream
      .fromIterable(0 to 100)
      .partition(_ % 2 == 0, buffer = 50)

  val partitioned: ZIO[
    Scope,
    Nothing,
    (ZStream[Any, Nothing, Int], ZStream[Any, Nothing, Int])
  ] =
    ZStream
      .fromIterable(1 to 10)
      .partitionEither(x => ZIO.succeed(if (x < 5) Left(x) else Right(x)))

  override def run = for {
    _ <- print("partitionResult: ") *> partitionResult
      .flatMap({ case (s1, s2) =>
        print("Left Partition: ") *> s1.runForeachChunk(printLine(_)) *> print(
          "Right Partition: "
        ) *> s2
          .runForeachChunk(printLine(_))
      })
    _ <- print("partitioned: ") *> partitioned
      .flatMap({ case (s1, s2) =>
        print("Left Partition: ") *> s1.runForeachChunk(printLine(_)) *> print(
          "Right Partition: "
        ) *> s2
          .runForeachChunk(printLine(_))
      })
  } yield ()
}
