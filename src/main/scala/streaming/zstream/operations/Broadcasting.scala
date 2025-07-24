package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._
import java.io.IOException

object Broadcasting extends ZIOAppDefault {
  val stream: ZIO[Any, IOException, Unit] =
    ZIO.scoped {
      ZStream
        .fromIterable(1 to 20)
        .mapZIO(_ => Random.nextInt)
        .map(Math.abs)
        .map(_ % 100)
        .tap(e => printLine(s"Emit $e element before broadcasting"))
        .broadcast(2, 5)
        .flatMap { streams =>
          for {
            out1 <- streams(0)
              .runFold(0)((acc, e) => Math.max(acc, e))
              .flatMap(x => printLine(s"Maximum: $x"))
              .fork
            out2 <- streams(1)
              .schedule(Schedule.spaced(1.second))
              .foreach(x => printLine(s"Logging to the Console: $x"))
              .fork
            _ <- out1.join.zipPar(out2.join)
          } yield ()
        }
    }

  override def run = for {
    _ <- print("stream: ") *> stream
  } yield ()
}
