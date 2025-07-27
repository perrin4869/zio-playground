package il.co.dotcore.zio.playground.streaming.zchannel.operations

import zio._
import zio.stream._

object Collecting extends ZIOAppDefault {
  override def run = for {
    _ <- Console.print("collectElements: ") *> ZChannel
      .writeAll(1, 2, 3, 4, 5)
      .collectElements
      .runCollect
      .debug
    // Output: (Chunk(),(Chunk(1,2,3,4,5),()))
    _ <- Console.print("emitCollect: ") *> ZChannel
      .writeAll(1, 2, 3, 4, 5)
      .emitCollect
      .runCollect
      .debug
// Output: (Chunk((Chunk(1,2,3,4,5),())),())
  } yield ()
}
