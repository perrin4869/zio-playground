package il.co.dotcore.zio.playground.streaming.zpipeline

import zio._
import zio.Console._
import zio.stream._

object Prepending extends ZIOAppDefault {

  val stream = ZStream(2, 3, 4).via(
    ZPipeline.prepend(Chunk(0, 1))
  )
  // Output: 0, 1, 2, 3, 4

  override def run = for {
    _ <- print("stream: ") *> stream.runForeachChunk(printLine(_))
  } yield ()
}
