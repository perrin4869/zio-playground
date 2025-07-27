package il.co.dotcore.zio.playground.streaming.zchannel.interruption

import zio._
import zio.stream._

object InterruptWhenZIO extends ZIOAppDefault {
  def randomNumbers: ZChannel[Any, Any, Any, Any, Nothing, Int, Nothing] =
    ZChannel
      .fromZIO(Random.nextIntBounded(100))
      .flatMap(ZChannel.write) *>
      ZChannel.fromZIO(ZIO.sleep(1.second)) *> randomNumbers

  override def run = randomNumbers
    .interruptWhen(ZIO.sleep(3.seconds).as("Done!"))
    .runCollect
    .debug
  // One output: (Chunk(84,57,70),Done!)
}
