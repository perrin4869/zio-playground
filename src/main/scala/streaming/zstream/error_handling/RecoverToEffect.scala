package il.co.dotcore.zio.playground.streaming.zstream.error_handling

import zio._
import zio.stream._

object RecoverToEffect extends ZIOAppDefault {

  val stream =
    (ZStream(1, 2, 3) ++ ZStream.dieMessage("Oh! Boom!") ++ ZStream(4, 5))
      .onError(_ =>
        Console
          .printLine(
            "Stream application closed! We are doing some cleanup jobs."
          )
          .orDie
      )

  override def run = for {
    _ <- Console.print("stream: ") *>
      stream.runForeachChunk(Console.printLine(_)) *> Console.print("\n")
  } yield ()
}
