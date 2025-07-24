package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._

object Changes extends ZIOAppDefault {
  val changes = ZStream(1, 1, 1, 2, 2, 3, 4).changes

  case class Event(partition: Long, offset: Long, metadata: String)
  // val events: ZStream[Any, Nothing, Event] = ZStream.fromIterable(???)
  val events: ZStream[Any, Nothing, Event] =
    ZStream.fromIterable(
      Iterable(
        Event(10L, 10L, "foo"),
        Event(10L, 10L, "bar"),
        Event(20L, 10L, "foo"),
        Event(10L, 10L, "foo")
      )
    )

  val uniques = events.changesWith((e1, e2) =>
    (e1.partition == e2.partition && e1.offset == e2.offset)
  )

  override def run = for {
    _ <- print("changes: ") *> changes
      .runForeachChunk(printLine(_))
    _ <- print("uniques: ") *> uniques
      .runForeachChunk(printLine(_))
  } yield ()
}
