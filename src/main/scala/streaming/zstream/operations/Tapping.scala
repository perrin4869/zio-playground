package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._
import java.io.IOException

object Tapping extends ZIOAppDefault {
  val stream: ZStream[Any, IOException, Int] =
    ZStream(1, 2, 3)
      .tap(x => printLine(s"before mapping: $x"))
      .map(_ * 2)
      .tap(x => printLine(s"after mapping: $x"))

  override def run = for {
    _ <- print("stream: ") *> stream
      .runForeachChunk(printLine(_))
  } yield ()
}
