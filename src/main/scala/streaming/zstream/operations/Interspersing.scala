package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._

object Interspersing extends ZIOAppDefault {
  val s1 = ZStream(1, 2, 3, 4, 5).intersperse(0)
  // Output: 1, 0, 2, 0, 3, 0, 4, 0, 5

  val s2 = ZStream("a", "b", "c", "d").intersperse("[", "-", "]")
  // Output: [, -, a, -, b, -, c, -, d]

  override def run = for {
    _ <- print(
      "Interespering.s1: "
    ) *> s1
      .runForeachChunk(printLine(_))
    _ <- print(
      "Interspering.s2: "
    ) *> s2
      .runForeachChunk(printLine(_))
  } yield ()
}
