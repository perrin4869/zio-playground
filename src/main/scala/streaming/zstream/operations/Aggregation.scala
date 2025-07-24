package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._

object Aggregation extends ZIOAppDefault {
  val stream = ZStream(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
  val s1 = stream.transduce(ZSink.collectAllN[Int](3))
  // Output Chunk(1,2,3), Chunk(4,5,6), Chunk(7,8,9), Chunk(10)

  val source =
    ZStream
      .iterate(1)(_ + 1)
      .take(200)
      .tap(x =>
        printLine(s"Producing Element $x")
          .schedule(Schedule.duration(1.second).jittered)
      )

  val sink =
    ZSink.foreach((e: Chunk[Int]) =>
      printLine(s"Processing batch of events: $e")
        .schedule(Schedule.duration(3.seconds).jittered)
    )

  val myApp =
    source.transduce(ZSink.collectAllN[Int](5)).run(sink)

  object Async {
    val myApp =
      source.aggregateAsync(ZSink.collectAllN[Int](5)).run(sink)
  }

  // dataStream.aggregateAsyncWithin(
  //   ZSink.collectAllN[Record](2000),
  //   Schedule.fixed(30.seconds)
  // )

  // val schedule: Schedule[Any, Option[Chunk[Record]], Long] =
  //   // Start off with 30-second timeouts as long as the batch size is < 1000
  //   Schedule
  //     .fixed(30.seconds)
  //     .whileInput[Option[Chunk[Record]]](
  //       _.getOrElse(Chunk.empty).length < 100
  //     ) andThen
  //     // and then, switch to a shorter jittered schedule for as long as batches remain over 1000
  //     Schedule
  //       .fixed(5.seconds)
  //       .jittered
  //       .whileInput[Option[Chunk[Record]]](
  //         _.getOrElse(Chunk.empty).length >= 1000
  //       )

  // dataStream
  //   .aggregateAsyncWithin(ZSink.collectAllN[Record](2000), schedule)

  override def run = for {
    _ <- print("s1: ") *> s1
      .runForeachChunk(printLine(_))
    _ <- print("myApp: ") *> myApp
    _ <- print("Async.myApp: ") *> Async.myApp
  } yield ()
}
