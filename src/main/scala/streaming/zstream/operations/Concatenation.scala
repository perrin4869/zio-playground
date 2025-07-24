package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._

object Concatenation extends ZIOAppDefault {
  val a = ZStream(1, 2, 3)
  val b = ZStream(4, 5)
  val c1 = a ++ b
  val c2 = a concat b
  val c3 = ZStream.concatAll(Chunk(a, b))

  val stream = ZStream(1, 2, 3).flatMap(x => ZStream.repeat(x).take(4))
  // Input:  1, 2, 3
  // Output: 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3

  val stream2 =
    ZStream(1, 2, 3).flatMapParSwitch(10)(x => ZStream.repeat(x).take(4))

  override def run = for {
    _ <- print("c1: ") *> c1
      .runForeachChunk(printLine(_))
    _ <- print("c2: ") *> c2
      .runForeachChunk(printLine(_))
    _ <- print("c3: ") *> c3
      .runForeachChunk(printLine(_))
    _ <- print("stream: ") *> stream
      .runForeachChunk(printLine(_))
    _ <- print("stream2: ") *> stream2
      .runForeachChunk(printLine(_))
  } yield ()
}
