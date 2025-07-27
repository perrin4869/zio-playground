package il.co.dotcore.zio.playground.streaming.zchannel.composing

import zio._
import zio.stream._

object SimpleEcho extends ZIOAppDefault {

  val producer =
    ZChannel.write(1)

  val consumer =
    ZChannel.readWith(
      (i: Int) => ZChannel.fromZIO(Console.printLine("Consumed: " + i)),
      (_: Any) => ZChannel.unit,
      (_: Any) => ZChannel.unit
    )

  val res = (producer >>> consumer).run
  // Output:
  // Consumed: 1

  override def run = for {
    _ <- res.debug
  } yield ()
}
