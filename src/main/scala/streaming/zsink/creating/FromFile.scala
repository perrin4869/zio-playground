package il.co.dotcore.zio.playground.streaming.zsink.creating

import zio._
import zio.stream._
import zio.Console._

object FromFile extends ZIOAppDefault {
  import java.nio.file.{Path, Paths}

  def fileSink(path: Path): ZSink[Any, Throwable, String, Byte, Long] =
    ZSink
      .fromPath(path)
      .contramapChunks[String](_.flatMap(_.getBytes))

  val result = ZStream("Hello", "ZIO", "World!")
    .intersperse("\n")
    // .run(fileSink(Paths.get("file.txt")))
    .run(fileSink(Paths.get("test.txt")))

  override def run = for {
    _ <- result.debug("number of bytes written to test.txt")
  } yield ()
}
