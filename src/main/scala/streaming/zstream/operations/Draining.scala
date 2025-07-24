package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._
import java.io.IOException

object Draining extends ZIOAppDefault {
  val s1: ZStream[Any, Nothing, Nothing] = ZStream(1, 2, 3, 4, 5).drain
  // Emitted Elements: <empty stream, it doesn't emit any element>

  val s2: ZStream[Any, IOException, Int] =
    ZStream
      .repeatZIO {
        for {
          nextInt <- Random.nextInt
          number = Math.abs(nextInt % 10)
          _ <- Console.printLine(s"random number: $number")
        } yield (number)
      }
      .take(3)
  // Emitted Elements: 1, 4, 7
  // Result of Stream Effect on the Console:
  // random number: 1
  // random number: 4
  // random number: 7

  val s3: ZStream[Any, IOException, Nothing] = s2.drain
  // Emitted Elements: <empty stream, it doesn't emit any element>
  // Result of Stream Effect on the Console:
  // random number: 4
  // random number: 8
  // random number: 2

  val logging = ZStream.fromZIO(
    printLine("Starting to merge with the next stream")
  )
  val stream = ZStream(1, 2, 3) ++ logging.drain ++ ZStream(4, 5, 6)

  // Emitted Elements: 1, 2, 3, 4, 5, 6
  // Result of Stream Effect on the Console:
  // Starting to merge with the next stream

  val stream2 = ZStream(1, 2, 3) ++ logging ++ ZStream(4, 5, 6)

  // Emitted Elements: 1, 2, 3, (), 4, 5, 6
  // Result of Stream Effect on the Console:
  // Starting to merge with the next stream

  override def run = for {
    _ <- print("s1: ") *> s1
      .runForeachChunk(printLine(_)) *> print("\n")
    _ <- print("s2: ") *> s2
      .runForeachChunk(printLine(_))
    _ <- print("s3: ") *> s3
      .runForeachChunk(printLine(_))
    _ <- print("stream: ") *> stream
      .runForeachChunk(printLine(_))
    _ <- print("stream2: ") *> stream2
      .runForeachChunk(printLine(_))
  } yield ()
}
