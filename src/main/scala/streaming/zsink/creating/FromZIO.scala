package il.co.dotcore.zio.playground.streaming.zsink.creating

import zio._
import zio.stream._
import zio.Console._

object FromZIO extends ZIOAppDefault {
  val sink = ZSink.fromZIO(ZIO.succeed(1))

  override def run = for {
    _ <- ZStream().run(sink).debug
  } yield ()
}
