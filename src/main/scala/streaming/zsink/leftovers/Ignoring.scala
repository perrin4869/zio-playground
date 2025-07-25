package il.co.dotcore.zio.playground.streaming.zsink.leftovers

import zio._
import zio.stream._
import zio.Console._

object Ignoring extends ZIOAppDefault {

  val sink = ZSink.take[Int](3).ignoreLeftover

  val stream = ZStream(1, 2, 3, 4, 5).transduce(
    ZSink.collectAllN[Int](2).ignoreLeftover
  )

  override def run = for {
    _ <- stream.runForeachChunk(printLine(_))
  } yield ()
}
