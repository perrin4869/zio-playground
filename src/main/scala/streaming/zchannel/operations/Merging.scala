package il.co.dotcore.zio.playground.streaming.zchannel.operations

import zio._
import zio.stream._

object Merging extends ZIOAppDefault {

  def iterate(
      from: Int,
      to: Int
  ): ZChannel[Any, Any, Any, Any, Nothing, Int, Unit] =
    if (from <= to)
      ZChannel.write(from) *>
        ZChannel.fromZIO(
          Random
            .nextLongBounded(1000)
            .flatMap(delay => ZIO.sleep(Duration.fromMillis(delay)))
        ) *> iterate(from + 1, to)
    else ZChannel.unit

  override def run =
    ZChannel
      .mergeAllUnbounded(
        ZChannel.writeAll(
          iterate(1, 3),
          iterate(4, 6),
          iterate(6, 9)
        )
      )
      .mapOutZIO(i => Console.print(i + " "))
      .runDrain
  // Sample output: 1 4 6 7 8 2 3 5 6 9
}
