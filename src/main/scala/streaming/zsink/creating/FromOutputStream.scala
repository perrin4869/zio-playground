package il.co.dotcore.zio.playground.streaming.zsink.creating

import zio._
import zio.stream._
import zio.Console._

object FromOutputStream extends ZIOAppDefault {
  override def run = for {
    _ <- ZStream("Application", "Error", "Logs")
      .intersperse("\n")
      .run(
        ZSink
          .fromOutputStream(java.lang.System.err)
          .contramapChunks[String](_.flatMap(_.getBytes))
      )
      .debug("number of bytes written to stderr")
  } yield ()
}
