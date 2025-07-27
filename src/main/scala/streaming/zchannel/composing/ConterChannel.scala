package il.co.dotcore.zio.playground.streaming.zchannel.composing

import zio._
import zio.stream._

object CounterChannel extends ZIOAppDefault {
  val counter = {
    def count(c: Int): ZChannel[Any, Any, Int, Any, String, Int, Int] =
      ZChannel.readWith(
        (i: Int) => ZChannel.write(i) *> count(c + 1),
        (_: Any) => ZChannel.fail("error"),
        (_: Any) => ZChannel.succeed(c)
      )

    count(0)
  }

  def run = (ZChannel.writeAll(1, 2, 3, 4, 5) >>> counter).runCollect.debug
}

// Output:
// (Chunk(1,2,3,4,5), 5)
