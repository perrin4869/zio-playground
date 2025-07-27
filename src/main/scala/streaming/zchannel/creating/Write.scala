package il.co.dotcore.zio.playground.streaming.zchannel.creating

import zio._
import zio.stream._

object Write extends ZIOAppDefault {
  override def run = for {
    _ <- ZChannel.write(1).runCollect.debug
    // Output: (Chunk(1),())
    _ <- ZChannel.writeAll(1, 2, 3).runCollect.debug
    // Output: (Chunk(1,2,3),())
    _ <- ZChannel.writeChunk(Chunk(1, 2, 3)).runCollect.debug
    // Output: (Chunk(1,2,3),())
  } yield ()
}
