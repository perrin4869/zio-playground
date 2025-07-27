package il.co.dotcore.zio.playground.streaming.zchannel.operations

import zio._
import zio.stream._
import zio.stream.ZChannel._

object MergeMap extends ZIOAppDefault {
  override def run = ZChannel
    .writeAll("a", "b", "c")
    .mergeMap(8, 1, MergeStrategy.BackPressure) { l =>
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
// Non-deterministic output: (Chunk(a0,a1,a2,b0,b1,b2,b3,c0,b4,c1,a3,c2,b5,a4,c3,c4,a5,c5),())
}
