package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._

object Cross extends ZIOAppDefault {
  val first = ZStream(1, 2, 3)
  val second = ZStream("a", "b")

  val s1 = first cross second
  val s2 = first <*> second
  val s3 = first.crossWith(second)((a, b) => (a, b))
  // Output: (1,a), (1,b), (2,a), (2,b), (3,a), (3,b)

  val s4 = first crossLeft second
  val s5 = first <* second
  // Keep only elements from the left stream
  // Output: 1, 1, 2, 2, 3, 3

  val s6 = first crossRight second
  val s7 = first *> second
  // Keep only elements from the right stream
  // Output: a, b, a, b, a, b

  override def run = for {
    _ <- print("s1: ") *> s1
      .runForeachChunk(printLine(_))
    _ <- print("s2: ") *> s2
      .runForeachChunk(printLine(_))
    _ <- print("s3: ") *> s3
      .runForeachChunk(printLine(_))
    _ <- print("s4: ") *> s4
      .runForeachChunk(printLine(_))
    _ <- print("s5: ") *> s5
      .runForeachChunk(printLine(_))
    _ <- print("s6: ") *> s6
      .runForeachChunk(printLine(_))
    _ <- print("s7: ") *> s7
      .runForeachChunk(printLine(_))
  } yield ()
}
