package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._

object Zipping extends ZIOAppDefault {
  val s1: UStream[(Int, String)] =
    ZStream(1, 2, 3, 4, 5, 6).zipWith(ZStream("a", "b", "c"))((a, b) => (a, b))

  val s2: UStream[(Int, String)] =
    ZStream(1, 2, 3, 4, 5, 6).zip(ZStream("a", "b", "c"))

  // Output: (1, "a"), (2, "b"), (3, "c")

  val s3 = ZStream(1, 2, 3)
    .zipAll(ZStream("a", "b", "c", "d", "e"))(0, "x")
  val s4 = ZStream(1, 2, 3).zipAllWith(
    ZStream("a", "b", "c", "d", "e")
  )(_ => 0, _ => "x")((a, b) => (a, b))

  // Output: (1, a), (2, b), (3, c), (0, d), (0, e)

  val s5 = ZStream(1, 2, 3)
    .schedule(Schedule.spaced(1.second))

  val s6 = ZStream("a", "b", "c", "d")
    .schedule(Schedule.spaced(500.milliseconds))
    .rechunk(3)

  val zipLatest = s5.zipLatest(s6)

  // Output: (1, a), (1, b), (1, c), (1, d), (2, d), (3, d)

  val stream: UStream[Int] = ZStream.fromIterable(1 to 5)

  val zipWithPrevious: UStream[(Option[Int], Int)] = stream.zipWithPrevious
  val zipWithNext: UStream[(Int, Option[Int])] = stream.zipWithNext
  val zipWithPreviousAndNext: UStream[(Option[Int], Int, Option[Int])] =
    stream.zipWithPreviousAndNext

  val indexedStream: ZStream[Any, Nothing, (String, Long)] =
    ZStream("Mary", "James", "Robert", "Patricia").zipWithIndex

  // Output: ("Mary", 0L), ("James", 1L), ("Robert", 2L), ("Patricia", 3L)

  override def run = for {
    _ <- print("s1: ") *> s1
      .runForeachChunk(printLine(_))
    _ <- print("s2: ") *> s2
      .runForeachChunk(printLine(_))
    _ <- print("s3: ") *> s3
      .runForeachChunk(printLine(_))
    _ <- print("s4: ") *> s4
      .runForeachChunk(printLine(_))
    _ <- print("zipLatest: ") *> zipLatest
      .runForeachChunk(printLine(_))
    _ <- print("zipWithPrevious: ") *> zipWithPrevious
      .runForeachChunk(printLine(_))
    _ <- print("zipWithNext: ") *> zipWithNext
      .runForeachChunk(printLine(_))
    _ <- print(
      "zipWithPreviousAndNext: "
    ) *> zipWithPreviousAndNext
      .runForeachChunk(printLine(_))
    _ <- print(
      "indexedStream: "
    ) *> indexedStream
      .runForeachChunk(printLine(_))
  } yield ()
}
