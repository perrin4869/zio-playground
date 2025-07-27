package il.co.dotcore.zio.playground.streaming.zchannel.operations

import zio._
import zio.stream._

object ConcatMap extends ZIOAppDefault {
  override def run = ZChannel
    .writeAll("a", "b", "c")
    .concatMap { l =>
      def inner(
          from: Int,
          to: Int
      ): ZChannel[Any, Any, Any, Any, Nothing, String, Unit] =
        if (from <= to) ZChannel.write(s"$l$from") *> inner(from + 1, to)
        else ZChannel.unit
      inner(0, 5)
    }
    .runCollect
    .debug
  // Output: (Chunk(a0,a1,a2,a3,a4,a5,b0,b1,b2,b3,b4,b5,c0,c1,c2,c3,c4,c5),())
}
