package il.co.dotcore.zio.playground.streaming.zchannel.composing

import zio._
import zio.stream._

object ReplicatorChannel extends ZIOAppDefault {
  lazy val doubler: ZChannel[Any, Any, Int, Any, Nothing, Int, Unit] =
    ZChannel.readWith(
      (i: Int) => ZChannel.writeAll(i, i) *> doubler,
      (_: Any) => ZChannel.unit,
      (_: Any) => ZChannel.unit
    )
  def run = (ZChannel.writeAll(1, 2, 3, 4, 5) >>> doubler).runCollect.debug
}
// Output:
//   (Chunk(1,1,2,2,3,3,4,4,5,5),())
