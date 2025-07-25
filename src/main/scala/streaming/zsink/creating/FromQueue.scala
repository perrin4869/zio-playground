package il.co.dotcore.zio.playground.streaming.zsink.creating

import zio._
import zio.stream._
import zio.Console._

object FromQueue extends ZIOAppDefault {
  import java.io.IOException

  val myApp: IO[IOException, Unit] =
    for {
      queue <- Queue.bounded[Int](32)
      producer <- ZStream
        .iterate(1)(_ + 1)
        .schedule(Schedule.fixed(200.millis))
        .run(ZSink.fromQueue(queue))
        .fork
      consumer <- queue.take.flatMap(printLine(_)).forever
      _ <- producer.zip(consumer).join
    } yield ()

  override def run = myApp.timeout(Duration.fromSeconds(10)).cause.debug
}
