package il.co.dotcore.zio.playground.streaming.zchannel.creating

import zio._
import zio.stream._
import zio.Console._

object Read extends ZIOAppDefault {
  val read: ZChannel[Any, Any, Int, Any, None.type, Nothing, Int] =
    ZChannel.read[Int]

  override def run = for {
    _ <- (ZChannel.write(1) >>> read).runCollect.debug
    // Output: (Chunk(0),1)
    _ <- (ZChannel.writeAll(1, 2, 3) >>> (read *> read)).runCollect.debug
    // Output: (Chunk(),2)
    _ <- (ZChannel.writeAll(
      1,
      2,
      3
    ) >>> (read *> read *> read)).runCollect.debug
    // Output: (Chunk(),3)
  } yield ()
}
