package il.co.dotcore.zio.playground.streaming.zchannel.operations

import zio._
import zio.stream._

object Piping extends ZIOAppDefault {
  override def run = (ZChannel.writeAll(1, 2, 3) >>> (ZChannel
    .read[Int] <*> ZChannel.read[Int])).runCollect.debug
}
