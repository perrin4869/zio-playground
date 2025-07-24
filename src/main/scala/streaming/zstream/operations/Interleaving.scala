package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._

object Interleaving extends ZIOAppDefault {
  object Interleave {
    val s1 = ZStream(1, 2, 3)
    val s2 = ZStream(4, 5, 6, 7, 8)

    val interleaved = s1 interleave s2

    // Output: 1, 4, 2, 5, 3, 6, 7, 8
  }

  object InterleaveWith {
    val s1 = ZStream(1, 3, 5, 7, 9)
    val s2 = ZStream(2, 4, 6, 8, 10)

    val interleaved =
      s1.interleaveWith(s2)(ZStream(true, false, false).forever)
    // Output: 1, 2, 4, 3, 6, 8, 5, 10, 7, 9
  }

  override def run = for {
    _ <- print(
      "Interleave.interleaved: "
    ) *> Interleave.interleaved
      .runForeachChunk(printLine(_))
    _ <- print(
      "InterleaveWith.interleaved: "
    ) *> InterleaveWith.interleaved
      .runForeachChunk(printLine(_))
  } yield ()
}
