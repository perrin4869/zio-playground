package il.co.dotcore.zio.playground.streaming.zpipeline

import zio._
import zio.Console._
import zio.stream._

object Splitting extends ZIOAppDefault {

  // TODO: https://github.com/zio/zio/issues/10050
  val stream1 = ZStream("1-2-3", "4-5", "6", "7-8-9-10")
    .via(ZPipeline.splitOn("-"))
    .map(_.toInt)
  // Ouput: 1, 2, 3, 4, 5, 6, 7, 8, 9 10

  val stream2 =
    ZStream("This is the first line.\nSecond line.\nAnd the last line.")
      .via(ZPipeline.splitLines)
  // Output: "This is the first line.", "Second line.", "And the last line."

  val stream3 =
    ZStream(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
      .via(ZPipeline.splitOnChunk(Chunk(4, 5, 6)))
  // Output: Chunk(1, 2, 3), Chunk(7, 8, 9, 10)

  override def run = for {
    _ <- print("stream1: ") *> stream1.runForeachChunk(printLine(_))
    _ <- print("stream2: ") *> stream2.foreach(printLine(_))
    _ <- print("stream3: ") *> stream3.runForeachChunk(printLine(_))
  } yield ()
}
