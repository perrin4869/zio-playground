package il.co.dotcore.zio.playground.streaming.zchannel.operations

import zio._
import zio.stream._

object Collect extends ZIOAppDefault {
  override def run = ZChannel
    .writeAll((1 to 10): _*)
    .collect { case i if i % 3 == 0 => i * 2 }
    .runCollect
    .debug
  // Output: (Chunk(6,12,18),())
}
