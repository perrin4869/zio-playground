package il.co.dotcore.zio.playground.streaming.zpipeline

import zio._
import zio.Console._
import zio.stream._

object Operations extends ZIOAppDefault {

  val numbers: ZStream[Any, Nothing, Int] =
    ZStream("1-2-3-4-5")
      .mapConcat(_.split("-"))
      .via(
        ZPipeline.map[String, Int](_.toInt)
      )

  val numbers2: ZStream[Any, Nothing, Int] =
    ZStream(new StringBuilder().append("20"))
      .via(
        ZPipeline
          .map[String, Int](_.toInt)
          .contramap[StringBuilder](_.toString())
      )

  val lines: ZStream[Any, Throwable, String] =
    ZStream
      .fromFileName("file.txt")
      .via(
        ZPipeline.utf8Decode >>> ZPipeline.splitLines
      )

  import java.nio.charset.CharacterCodingException
  val refine: ZIO[Any, Throwable, Long] = {
    val stream: ZStream[Any, Throwable, Byte] = ZStream.fromFileName("file.txt")
    val pipeline: ZPipeline[Any, CharacterCodingException, Byte, String] =
      ZPipeline.utf8Decode >>> ZPipeline.splitLines >>> ZPipeline
        .filter[String](_.contains('₿'))
    val fileSink: ZSink[Any, Throwable, String, Byte, Long] = ZSink
      .fromFileName("file.refined.txt")
      .contramapChunks[String](
        _.flatMap(line => (line + System.lineSeparator).getBytes())
      )
    val pipeSink: ZSink[Any, Throwable, Byte, Byte, Long] =
      pipeline >>> fileSink
    stream >>> pipeSink
  }

  override def run = for {
    _ <- print("numbers: ") *> numbers.foreach(printLine(_))
    _ <- print("numbers2: ") *> numbers2.foreach(printLine(_))
    _ <- print("lines: ") *> lines.foreach(printLine(_))
    _ <- print("refine: ") *> refine
  } yield ()
}
