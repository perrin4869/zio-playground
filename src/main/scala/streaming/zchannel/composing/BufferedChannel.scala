package il.co.dotcore.zio.playground.streaming.zchannel.composing

import zio._
import zio.stream._

import scala.collection.immutable.HashSet

object BufferedChannel extends ZIOAppDefault {
  def buffered(input: Int) =
    ZChannel
      .fromZIO(Ref.make(input))
      .flatMap { ref =>
        ZChannel.buffer[Any, Int, Unit](
          0,
          i => if (i == 0) true else false,
          ref
        )
      }

  def run =
    (ZChannel.write(1) >>> buffered(0)).runCollect.debug
  // (ZChannel.write(1) >>> buffered(5)).runCollect.debug
}
