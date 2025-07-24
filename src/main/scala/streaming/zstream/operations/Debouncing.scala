package il.co.dotcore.zio.playground.streaming.zstream.operations

import zio._
import zio.stream._
import zio.Console._

object Debouncing extends ZIOAppDefault {
  val stream = (
    ZStream(1, 2, 3) ++
      ZStream.fromZIO(ZIO.sleep(500.millis)) ++ ZStream(4, 5) ++
      ZStream.fromZIO(ZIO.sleep(10.millis)) ++
      ZStream(6)
  ).debounce(100.millis) // emit only after a pause of at least 100 ms
  // Output: 3, 6

  override def run = for {
    _ <- print("stream: ") *> stream
      .runForeachChunk(printLine(_))
  } yield ()
}
