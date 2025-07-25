package il.co.dotcore.zio.playground.streaming.zsink.creating

import zio._
import zio.stream._
import zio.Console._

object FromHub extends ZIOAppDefault {
  import java.io.IOException

  val myApp: ZIO[Any, IOException, Unit] =
    for {
      promise <- Promise.make[Nothing, Unit]
      hub <- Hub.bounded[Int](1)
      sink <- ZIO.succeed(ZSink.fromHub(hub))
      producer <- ZStream
        .iterate(0)(_ + 1)
        .schedule(Schedule.fixed(1.seconds))
        .run(sink)
        .fork
      consumers <- ZIO.scoped {
        hub.subscribe.zip(hub.subscribe).flatMap { case (left, right) =>
          for {
            _ <- promise.succeed(())
            f1 <- left.take
              .flatMap(e => printLine(s"Left Queue: $e"))
              .forever
              .fork
            f2 <- right.take
              .flatMap(e => printLine(s"Right Queue: $e"))
              .forever
              .fork
            _ <- f1.zip(f2).join
          } yield ()
        }
      }.fork
      _ <- promise.await
      _ <- producer.zip(consumers).join
    } yield ()

  override def run = myApp.timeout(Duration.fromSeconds(10)).cause.debug
}
