package il.co.dotcore.zio.playground.streaming.zsink.operations

import zio._
import zio.stream._
import zio.Console._

object Filtering extends ZIOAppDefault {

  val stream = ZStream(1, -2, 0, 1, 3, -3, 4, 2, 0, 1, -3, 1, 1, 6)
    .transduce(
      ZSink
        .collectAllN[Int](3)
        .filterInput[Int](_ > 0)
    )
  // Output: Chunk(Chunk(1,1,3),Chunk(4,2,1),Chunk(1,1,6),Chunk())

  override def run = for {
    _ <- stream.runForeachChunk(printLine(_))
  } yield ()
}
