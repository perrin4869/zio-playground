package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._

object Merging extends ZIOAppDefault {
  val s1 = ZStream(1, 2, 3).rechunk(1)
  val s2 = ZStream(4, 5, 6).rechunk(1)

  val merged = s1 merge s2
  // As the merge operation is not deterministic, it may output the following stream of numbers:
  // Output: 4, 1, 2, 5, 6, 3

  object TerminationStrategy {
    import zio.stream.ZStream.HaltStrategy
    val s1 = ZStream.iterate(1)(_ + 1).take(5).rechunk(1)
    val s2 = ZStream.repeat(0).rechunk(1)

    val merged = s1.merge(s2, HaltStrategy.Left)
  }

  val kafkaConsumer: UStream[Any] = ZStream(1)
  val httpServer: UIO[Any] = ZIO.succeed("omg")
  val scheduledJobRunner: UIO[Any] = ZIO.succeed("omg")

  val main =
    kafkaConsumer.runDrain.fork *>
      httpServer.fork *>
      scheduledJobRunner.fork *>
      ZIO.never

  // one option
  val main1 =
    ZIO.raceFirst(
      kafkaConsumer.runDrain,
      List(httpServer, scheduledJobRunner)
    )

  // another option
  val main2 =
    for {
      // _ <- other resources
      _ <- ZStream
        .mergeAllUnbounded(16)(
          kafkaConsumer.drain,
          ZStream.fromZIO(httpServer),
          ZStream.fromZIO(scheduledJobRunner)
        )
        .runDrain
    } yield ()

  object MergeWith {
    val s1 = ZStream("1", "2", "3")
    val s2 = ZStream(4.1, 5.3, 6.2)

    val merged = s1.mergeWith(s2)(_.toInt, _.toInt)
  }

  override def run = for {
    _ <- print("merged: ") *> merged
      .runForeachChunk(printLine(_))
    _ <- print(
      "TerminationStrategy.merged: "
    ) *> TerminationStrategy.merged
      .runForeachChunk(printLine(_))
    _ <- print(
      "MergeWith.merged: "
    ) *> MergeWith.merged
      .runForeachChunk(printLine(_))
  } yield ()
}
