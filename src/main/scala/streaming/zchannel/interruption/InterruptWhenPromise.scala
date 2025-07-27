package il.co.dotcore.zio.playground.streaming.zchannel.interruption

import zio._
import zio.stream._

object InterruptWhenPromise extends ZIOAppDefault {
  def randomNumbers: ZChannel[Any, Any, Any, Any, Nothing, Int, Nothing] =
    ZChannel
      .fromZIO(Random.nextIntBounded(100))
      .flatMap(ZChannel.write) *>
      ZChannel.fromZIO(ZIO.sleep(1.second)) *> randomNumbers

  override def run = for {
    p <- Promise.make[Nothing, Unit]
    f <- randomNumbers
      .interruptWhen(p)
      .mapOutZIO(e => Console.printLine(e))
      .runDrain
      .fork
    _ <- p.succeed(()).delay(5.seconds)
    _ <- f.join
  } yield ()

  // Output:
  // 74
  // 60
  // 52
  // 52
  // 79
}
