package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._

object Distribution extends ZIOAppDefault {
  val partitioned
      : ZIO[Scope, Nothing, (UStream[Int], UStream[Int], UStream[Int])] =
    ZStream
      .iterate(1)(_ + 1)
      .schedule(Schedule.fixed(1.seconds))
      .distributedWith(3, 10, x => ZIO.succeed(q => x % 3 == q))
      .flatMap {
        case q1 :: q2 :: q3 :: Nil =>
          ZIO.succeed(
            ZStream.fromQueue(q1).flattenExitOption,
            ZStream.fromQueue(q2).flattenExitOption,
            ZStream.fromQueue(q3).flattenExitOption
          )
        case _ => ZIO.dieMessage("Impossible!")
      }

  override def run = for {
    _ <- print(
      "partitioned: "
    ) *> partitioned.flatMap({ case (s1, s2, s3) =>
      print("Running the three partitioned streams: ") *>
        (s1
          .take(10)
          .runForeachChunk(c => print("s1: ") *> printLine(c)) <&> s2
          .take(10)
          .runForeachChunk(c => print("s2: ") *> printLine(c)) <&> s3
          .take(10)
          .runForeachChunk(c => print("s3: ") *> printLine(c)))
    })
  } yield ()
}
