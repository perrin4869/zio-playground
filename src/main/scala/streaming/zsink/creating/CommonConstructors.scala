package il.co.dotcore.zio.playground.streaming.zsink.creating

import zio._
import zio.stream._
import zio.Console._

object CommonConstructors extends ZIOAppDefault {
  object Head {
    val sink: ZSink[Any, Nothing, Int, Int, Option[Int]] = ZSink.head[Int]
    val head: ZIO[Any, Nothing, Option[Int]] = ZStream(1, 2, 3, 4).run(sink)
    // Result: Some(1)
  }

  object Last {
    val sink: ZSink[Any, Nothing, Int, Nothing, Option[Int]] = ZSink.last[Int]
    val last: ZIO[Any, Nothing, Option[Int]] = ZStream(1, 2, 3, 4).run(sink)
    // Result: Some(4)
  }

  object Count {
    val sink: ZSink[Any, Nothing, Int, Nothing, Long] = ZSink.count
    val count: ZIO[Any, Nothing, Long] = ZStream(1, 2, 3, 4, 5).run(sink)
    // Result: 5
  }

  object Sum {
    val sink: ZSink[Any, Nothing, Int, Nothing, Int] = ZSink.sum[Int]
    val sum: ZIO[Any, Nothing, Int] = ZStream(1, 2, 3, 4, 5).run(sink)
    // Result: 15
  }

  object Take {
    val sink: ZSink[Any, Nothing, Int, Int, Chunk[Int]] = ZSink.take[Int](3)
    val stream: ZIO[Any, Nothing, Chunk[Int]] = ZStream(1, 2, 3, 4, 5).run(sink)
    // Result: Chunk(1, 2, 3)
  }

  object Timed {
    val timed: ZSink[Any, Nothing, Any, Nothing, Duration] = ZSink.timed
    val stream: ZIO[Any, Nothing, Long] =
      ZStream(1, 2, 3, 4, 5)
        .schedule(Schedule.fixed(2.seconds))
        .run(timed)
        .map(_.getSeconds)
    // Result: 10
  }

  object ForEach {
    import java.io.IOException

    val printer: ZSink[Any, IOException, Int, Int, Unit] =
      ZSink.foreach((i: Int) => printLine(i))
    val stream: ZIO[Any, IOException, Unit] =
      ZStream(1, 2, 3, 4, 5).run(printer)
  }

  override def run = for {
    _ <- Head.head.debug("Head.head")
    _ <- Last.last.debug("Last.last")
    _ <- Count.count.debug("Count.count")
    _ <- Sum.sum.debug("Sum.sum")
    _ <- Take.stream.debug("Take.stream")
    _ <- ZStream(1, 2, 3).run(ZSink.drain).debug("drain")
    _ <- Timed.stream.debug("Timed.stream")
    _ <- ForEach.stream.debug("ForEach.stream")
  } yield ()
}
