package il.co.dotcore.zio.playground.streaming.zstream.error_handling

import zio._
import zio.Console._
import zio.stream._

object RecoverFromFailure extends ZIOAppDefault {

  val s1 = ZStream(1, 2, 3) ++ ZStream.fail("Oh! Error!") ++ ZStream(4, 5)
  val s2 = ZStream(6, 7, 8)

  val stream = s1.orElse(s2)
  // Output: 1, 2, 3, 6, 7, 8

  val stream2 = s1.orElseEither(s2)
  // Output: Left(1), Left(2), Left(3), Right(6), Right(7), Right(8)

  val first =
    ZStream(1, 2, 3) ++
      ZStream.fail("Uh Oh!") ++
      ZStream(4, 5) ++
      ZStream.fail("Ouch")

  val second = ZStream(6, 7, 8)
  val third = ZStream(9, 10, 11)

  val stream3 = first.catchAll {
    case "Uh Oh!" => second
    case "Ouch"   => third
  }
  // Output: 1, 2, 3, 6, 7, 8

  override def run = for {
    _ <- print("stream: ") *>
      stream.runForeachChunk(printLine(_)) *> print("\n")
    _ <- print("stream2: ") *>
      stream2.runForeachChunk(printLine(_)) *> print("\n")
    _ <- print("stream3: ") *>
      stream3.runForeachChunk(printLine(_)) *> print("\n")
  } yield ()
}
