package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._
import java.io.IOException

object Buffering extends ZIOAppDefault {
  val stream: ZStream[Any, IOException, Int] =
    ZStream
      .fromIterable(1 to 10)
      .rechunk(1)
      .tap(x => Console.printLine(s"before buffering: $x"))
      .buffer(4)
      .tap(x => Console.printLine(s"after buffering: $x"))
      .schedule(Schedule.spaced(5.second))

  override def run = for {
    _ <- print("stream: ") *> stream
      .runForeachChunk(printLine(_))
  } yield ()
}
